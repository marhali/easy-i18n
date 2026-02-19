package de.marhali.easyi18n.core.domain.event;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is published when the contents of a module within the project has been changed.
 *
 * @param moduleId Module identifier
 *
 * @author marhali
 */
public record ModuleChanged(
    @NotNull ModuleId moduleId
    ) implements DomainEvent {
}
