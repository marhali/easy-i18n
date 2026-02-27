package de.marhali.easyi18n.core.domain.event;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Event that is published when the contents of a module within the project has been changed.
 *
 * @param moduleId Module identifier
 * @param key Optional translation key that has been affected
 *
 * @author marhali
 */
public record ModuleChanged(
    @NotNull ModuleId moduleId,
    @Nullable I18nKey key
    ) implements DomainEvent {
}
