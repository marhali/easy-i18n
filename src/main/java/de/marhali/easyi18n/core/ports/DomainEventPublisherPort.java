package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.event.DomainEvent;
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
     * @param event Domain event to publish
     */
    void publish(@NotNull DomainEvent event);
}
