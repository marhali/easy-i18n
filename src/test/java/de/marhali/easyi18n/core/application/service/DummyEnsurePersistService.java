package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class DummyEnsurePersistService implements EnsurePersistService {
    @Override
    public void ensurePersist(@NotNull ModuleId moduleId) {
        // This is a dummy implementation that does nothing
    }
}
