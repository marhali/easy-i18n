package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class DummyEnsureLoadedService implements EnsureLoadedService {
    @Override
    public void ensureLoaded(@NotNull ModuleId moduleId) {
        // This is a dummy implementation that does nothing
    }
}
