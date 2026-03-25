package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nModule;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public class DummyModulePersistor implements ModulePersistor {
    @Override
    public void persistFrom(@NotNull ProjectConfigModule configModule, @NotNull I18nModule store) {
        // This is a dummy implementation that does nothing
    }
}
