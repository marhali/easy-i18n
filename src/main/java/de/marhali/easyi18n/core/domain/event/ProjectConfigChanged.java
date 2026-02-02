package de.marhali.easyi18n.core.domain.event;

import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Event that is published when the configuration of a specific project has been changed.
 *
 * @param projectId Project identifier
 */
public record ProjectConfigChanged(
    @NotNull ProjectId projectId
    ) implements DomainEvent {
}
