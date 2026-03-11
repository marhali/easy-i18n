package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.util.Alarm;
import de.marhali.easyi18n.core.application.command.EnsureModuleLoadedCommand;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for scheduling module loading using debounce mechanism.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
public final class ScheduledModuleLoaderService {

    private static final @NotNull Logger LOGGER = Logger.getInstance(ScheduledModuleLoaderService.class);

    private final static int DEBOUNCE_MS = 400;

    private final @NotNull Project project;
    private final @NotNull Map<@NotNull ModuleId, @NotNull Alarm> alarms;

    public ScheduledModuleLoaderService(@NotNull Project project) {
        this.project = project;
        this.alarms = new HashMap<>();
    }

    /**
     * Schedules the provided module to being loaded later.
     * @param moduleId Module identifier
     */
    public void loadModule(@NotNull ModuleId moduleId) {
        I18nProjectService projectService = project.getService(I18nProjectService.class);
        Alarm alarm = alarms.computeIfAbsent(moduleId, (_moduleId) -> new Alarm(Alarm.ThreadToUse.POOLED_THREAD, projectService));
        alarm.cancelAllRequests();
        alarm.addRequest(() -> internalLoadModuleAsync(moduleId), DEBOUNCE_MS);
    }

    private void internalLoadModuleAsync(@NotNull ModuleId moduleId) {
        PluginExecutorService executorService = project.getService(PluginExecutorService.class);
        I18nProjectService projectService = project.getService(I18nProjectService.class);

        executorService.runAsync(
            () -> {
                projectService.command(new EnsureModuleLoadedCommand(moduleId));
                return null;
            },
            (_void) -> alarms.remove(moduleId),
            this::handleThrowable,
            ModalityState.nonModal(),
            (o) -> project.isDisposed()
        );
    }

    private void handleThrowable(@NotNull Throwable throwable) {
        LOGGER.error(throwable);
    }
}
