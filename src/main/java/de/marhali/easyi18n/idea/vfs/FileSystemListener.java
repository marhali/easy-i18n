package de.marhali.easyi18n.idea.vfs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.Alarm;
import de.marhali.easyi18n.core.application.command.ModuleI18nPathsChangedCommand;
import de.marhali.easyi18n.core.application.service.I18nPathDetector;
import de.marhali.easyi18n.core.domain.model.ModuleI18nPath;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Virtual File System listener to detect changes that affect translation files.
 *
 * @author marhali
 */
public class FileSystemListener implements AsyncFileListener {

    private static final @NotNull Logger LOGGER = Logger.getInstance(FileSystemListener.class);

    private static final int DEBOUNCE_MS = 400;

    private final @NotNull Project project;
    private final @NotNull I18nPathDetector i18nPathDetector;

    private final @NotNull Set<@NotNull ModuleI18nPath> pendingChanges;
    private final @NotNull Alarm pendingChangesDebounceAlarm;

    public FileSystemListener(@NotNull Project project, @NotNull Disposable parentDisposable, @NotNull I18nPathDetector i18nPathDetector) {
        this.project = project;
        this.i18nPathDetector = i18nPathDetector;

        this.pendingChanges = ConcurrentHashMap.newKeySet();
        this.pendingChangesDebounceAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, parentDisposable);

        VirtualFileManager.getInstance().addAsyncFileListener(this, parentDisposable);
    }

    @Override
    public @Nullable ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> events) {
        ProgressManager.checkCanceled();

        Set<ModuleI18nPath> matchedPaths = new HashSet<>();

        for (VFileEvent event : events) {
            ProgressManager.checkCanceled();

            // We skip events that:
            // - that are requested by our own file system adapter (we already now these changes)
            // - that have no associated virtual file
            // - that are not in our current project
            if (event.getRequestor() instanceof FileSystemAdapter
                || event.getFile() == null
                || !ProjectFileIndex.getInstance(project).isInProject(event.getFile())) {
                continue;
            }

            String path = event.getPath();
            var moduleI18nPath = i18nPathDetector.detectModuleI18nPath(path);

            if (moduleI18nPath != null) {
                matchedPaths.add(moduleI18nPath);
            }
        }

        if (matchedPaths.isEmpty()) {
            // Skip ChangeApplier if we did not find any relevant files
            return null;
        }

        return new ChangeApplier() {
            @Override
            public void afterVfsChange() {
                pendingChanges.addAll(matchedPaths);
                scheduleFlush();
            }
        };
    }

    private void scheduleFlush() {
        pendingChangesDebounceAlarm.cancelAllRequests();
        pendingChangesDebounceAlarm.addRequest(this::flushChanges, DEBOUNCE_MS);
    }

    private void flushChanges() {
        Set<@NotNull ModuleI18nPath> batch = drainPending();
        if (batch.isEmpty()) return;

        PluginExecutorService executorService = project.getService(PluginExecutorService.class);
        I18nProjectService projectService = project.getService(I18nProjectService.class);

        executorService.runAsync(
            () -> {
                projectService.command(new ModuleI18nPathsChangedCommand(batch));
                return null;
            },
            (_void) -> {},
            this::handleThrowable,
            ModalityState.nonModal(),
            (o) -> project.isDisposed()
        );
    }

    private @NotNull Set<@NotNull ModuleI18nPath> drainPending() {
        HashSet<@NotNull ModuleI18nPath> out = new HashSet<>(pendingChanges);
        pendingChanges.removeAll(out);
        return out;
    }

    private void handleThrowable(@NotNull Throwable throwable) {
        LOGGER.error(throwable);
    }
}
