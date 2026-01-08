package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.event.DomainEvent;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Port for publishing domain events.
 *
 * @author marhali
 */
public interface DomainEventPublisherPort {
    /**
     * Publishes the provided domain event under the specified project scope.
     *
     * @param projectId Project identifier
     * @param event Domain event to publish
     */
    void publish(@NotNull ProjectId projectId, @NotNull DomainEvent event);
}
