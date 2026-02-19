package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

/**
 * Services that is mandatory for ensuring that requested modules within the project are loaded.
 *
 * @author marhali
 */
public final class EnsureLoadedService {

    private final @NotNull I18nStore store;
    private final @NotNull ProjectConfigPort projectConfigPort;
    private final @NotNull ModuleLoader moduleLoader;

    public EnsureLoadedService(
        @NotNull I18nStore store,
        @NotNull ProjectConfigPort projectConfigPort,
        @NotNull ModuleLoader moduleLoader
    ) {
        this.store = store;
        this.projectConfigPort = projectConfigPort;
        this.moduleLoader = moduleLoader;
    }

    /**
     * Ensures that the provided module is loaded.
     *
     * @param moduleId Module identifier
     */
    public void ensureLoaded(@NotNull ModuleId moduleId) {
        ProjectConfig projectConfig = projectConfigPort.read();
        ProjectConfigModule moduleConfig = projectConfig.modules().get(moduleId);

        if (moduleConfig == null) {
            throw new IllegalArgumentException("Module " + moduleId + " is not configured");
        }

        if (store.getSnapshot().hasModule(moduleId)) {
            // Module is already loaded
            return;
        }

        store.mutate(project -> {
            // Get or crate target module
            MutableI18nModule module = project.getOrCreateModule(moduleId);

            // Erase any existing state
            module.clear();

            // Load data from persistence layer
            moduleLoader.loadInto(moduleConfig, module);
        });
    }
}
