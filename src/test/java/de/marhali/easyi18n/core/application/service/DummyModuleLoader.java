package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class DummyModuleLoader implements ModuleLoader {
    @Override
    public void loadInto(@NotNull ProjectConfigModule config, @NotNull MutableI18nModule store) {
        // This is a dummy implementation that does nothing
    }
}
