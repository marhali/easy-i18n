package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

/**
 * Project-specific service which handles the execution of workloads.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
public final class PluginExecutorService {

    private final @NotNull Project project;

    public PluginExecutorService(@NotNull Project project) {
        this.project = project;
    }

    public <T> @NotNull CompletableFuture<T> runAsync(
        @NotNull Callable<T> onRunAsync,
        @NotNull Consumer<? super T> onSuccess,
        @NotNull Consumer<? super Throwable> onFailure,
        @NotNull ModalityState modalityState,
        @NotNull Condition<?> expired
        ) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return onRunAsync.call();
            } catch (ProcessCanceledException e) {
                throw e;
            } catch (Throwable e) {
                throw new CompletionException(e);
            }
        }, AppExecutorUtil.getAppExecutorService());

        future.whenComplete((result, error) ->
            ApplicationManager.getApplication().invokeLater(() -> {

            if (error == null) {
                onSuccess.accept(result);
                return;
            }

            Throwable throwable = (error instanceof CompletionException ce && ce.getCause() != null)
                ? ce.getCause()
                : error;

            onFailure.accept(throwable);

        }, modalityState, expired));

        return future;
    }
}
