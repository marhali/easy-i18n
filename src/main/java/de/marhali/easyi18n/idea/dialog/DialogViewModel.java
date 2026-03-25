package de.marhali.easyi18n.idea.dialog;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.util.Condition;
import com.intellij.util.Alarm;

import de.marhali.easyi18n.core.application.command.AddI18nRecordCommand;
import de.marhali.easyi18n.core.application.command.RemoveI18nRecordCommand;
import de.marhali.easyi18n.core.application.command.UpdateI18nRecordCommand;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.query.ModuleLocalesQuery;
import de.marhali.easyi18n.core.application.query.TranslationByKeyQuery;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Underlying view model behind a {@link TranslationDialog}.
 * Responsible for interacting with the domain core.
 *
 * @author marhali
 */
public final class DialogViewModel {

    private final @NotNull I18nProjectService projectService;
    private final @NotNull PluginExecutorService executorService;
    private final @NotNull ModuleId moduleId;
    private final @NotNull ModalityState modalityState;
    private final @NotNull Condition<? super Object> disposed;
    private final @NotNull Alarm existingKeyQueryDebounceAlarm;
    private final @NotNull AtomicLong keyCheckSequence;

    public DialogViewModel(
        @NotNull I18nProjectService projectService,
        @NotNull PluginExecutorService executorService,
        @NotNull ModuleId moduleId,
        @NotNull ModalityState modalityState,
        @NotNull Disposable disposable,
        @NotNull Condition<? super Object> disposed
    ) {
        this.projectService = projectService;
        this.executorService = executorService;
        this.moduleId = moduleId;
        this.modalityState = modalityState;
        this.disposed = disposed;
        this.existingKeyQueryDebounceAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD, disposable);
        this.keyCheckSequence = new AtomicLong(0);
    }

    /**
     * Loads all target locales for the current module.
     * @param onSuccess Callback with locales on success
     * @param onFailure Callback with throwable on error
     */
    public void loadLocalesAsync(@NotNull Consumer<Set<LocaleId>> onSuccess, @NotNull Consumer<Throwable> onFailure) {
        executorService.runAsync(
            () -> projectService.query(new ModuleLocalesQuery(moduleId)),
            onSuccess,
            onFailure,
            modalityState,
            disposed
        );
    }

    /**
     * Checks whether the specified translation key already exists or not.
     * @param key Translation key
     * @param onSuccess Callback with boolean (exists) on success
     * @param onFailure Callback with throwable on error
     */
    public void checkI18nKeyExistsAsync(
        @NotNull I18nKey key,
        @NotNull Consumer<Boolean> onSuccess,
        @NotNull Consumer<Throwable> onFailure
    ) {
       existingKeyQueryDebounceAlarm.cancelAllRequests();
       existingKeyQueryDebounceAlarm.addRequest(() -> {
           long sequenceToken = keyCheckSequence.incrementAndGet();
           executorService.runAsync(
               () -> projectService.query(new TranslationByKeyQuery(moduleId, key)),
               (result) -> onSuccess.accept(result.isPresent()),
               onFailure,
               modalityState,
               (o) -> disposed.test(o) || sequenceToken != keyCheckSequence.get()
           );
       }, 250);
    }

    /**
     * Saves the provided translation.
     * @param mode Dialog mode
     * @param entry Translation state to save
     * @param originEntry Nullable origin translation state
     * @param onSuccess Empty callback on success
     * @param onFailure Callback with throwable on error
     */
    public void saveAsync(
        @NotNull TranslationDialogMode mode,
        @NotNull I18nEntry entry,
        @NotNull NullableI18nEntry originEntry,
        @NotNull Consumer<Void> onSuccess,
        @NotNull Consumer<Throwable> onFailure
    ) {
        Command command = mode == TranslationDialogMode.ADD
            ? new AddI18nRecordCommand(moduleId, entry.key(), entry.content())
            : new UpdateI18nRecordCommand(moduleId, Objects.requireNonNull(originEntry.key(), "Origin key must be not null for update command"), entry.key(), entry.content());

        executorService.runAsync(
            () -> {
                projectService.command(command);
                return null;
            },
            (_void) -> onSuccess.accept(null),
            onFailure,
            modalityState,
            disposed
        );
    }

    /**
     * Deletes the provided translation.
     * @param key Translation key
     * @param onSuccess Empty callback on success
     * @param onFailure Callback with throwable on error
     */
    public void deleteAsync(
        @NotNull I18nKey key,
        @NotNull Consumer<Void> onSuccess,
        @NotNull Consumer<Throwable> onFailure
        ) {
        executorService.runAsync(
            () -> {
                projectService.command(new RemoveI18nRecordCommand(moduleId, key));
                return null;
            },
            onSuccess,
            onFailure,
            modalityState,
            disposed
        );
    }
}
