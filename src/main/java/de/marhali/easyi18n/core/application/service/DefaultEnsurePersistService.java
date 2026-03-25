package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

/**
 * Default implementation for {@link EnsurePersistService}
 * that uses the {@link ModulePersistor} to actually persist the module.
 *
 * @author marhali
 */
public final class DefaultEnsurePersistService implements EnsurePersistService{

    private final @NotNull I18nStore store;
    private final @NotNull ProjectConfigPort projectConfigPort;
    private final @NotNull ModulePersistor modulePersistor;

    public DefaultEnsurePersistService(@NotNull I18nStore store, @NotNull ProjectConfigPort projectConfigPort, @NotNull ModulePersistor modulePersistor) {
        this.store = store;
        this.projectConfigPort = projectConfigPort;
        this.modulePersistor = modulePersistor;
    }

    public void ensurePersist(@NotNull ModuleId moduleId) {
        ProjectConfig projectConfig = projectConfigPort.read();
        ProjectConfigModule moduleConfig = projectConfig.modules().get(moduleId);

        if (moduleConfig == null) {
            throw new IllegalArgumentException("Module'" + moduleId + " is not configured");
        }

        store.holdSnapshot((project) -> {
            I18nModule module = project.getModule(moduleId);

            if (module == null) {
                throw new IllegalStateException("Module " + moduleId + " is not loaded");
            }

            modulePersistor.persistFrom(moduleConfig, module);
        });
    }
}
