package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nModule;
import org.jetbrains.annotations.NotNull;

/**
 * Module persistor service definition.
 *
 * @author marhali
 */
public interface ModulePersistor {
    /**
     * Persists the specified module from the domain-level store to the persistence layer.
     *
     * @param configModule Module config
     * @param store Module store
     */
    void persistFrom(@NotNull ProjectConfigModule configModule, @NotNull I18nModule store);
}
