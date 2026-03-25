package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import org.jetbrains.annotations.NotNull;

/**
 * Module loader service definition.
 *
 * @author marhali
 */
public interface ModuleLoader {
    /**
     * Loads the specified module from the persistence layer into the given store.
     * @param config Module config
     * @param store Module store
     */
    void loadInto(@NotNull ProjectConfigModule config, @NotNull MutableI18nModule store);
}
