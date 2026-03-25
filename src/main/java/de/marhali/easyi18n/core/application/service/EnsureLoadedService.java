package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Definition for a service to ensure that a requested module is loaded.
 *
 * @author marhali
 */
public interface EnsureLoadedService {
    /**
     * Ensures that the provided module is loaded.
     *
     * @param moduleId Module identifier
     */
    void ensureLoaded(@NotNull ModuleId moduleId);
}
