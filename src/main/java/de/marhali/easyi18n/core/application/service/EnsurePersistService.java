package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Definition for a service to ensure that a requested module is persisted.
 *
 * @author marhali
 */
public interface EnsurePersistService {
    /**
     * Ensures that the provided module is persisted.
     *
     * @param moduleId Module identifier
     */
    void ensurePersist(@NotNull ModuleId moduleId);
}
