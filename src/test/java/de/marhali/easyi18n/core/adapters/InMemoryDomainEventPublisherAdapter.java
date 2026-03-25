package de.marhali.easyi18n.core.adapters;

import de.marhali.easyi18n.core.domain.event.DomainEvent;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marhali
 */
public class InMemoryDomainEventPublisherAdapter implements DomainEventPublisherPort {

    private final @NotNull List<@NotNull DomainEvent> events;

    public InMemoryDomainEventPublisherAdapter() {
        this.events = new ArrayList<>();
    }

    @Override
    public void publish(@NotNull DomainEvent event) {
        events.add(event);
    }

    public @NotNull List<@NotNull DomainEvent> getEvents() {
        return events;
    }

    public @NotNull DomainEvent getLastEvent() {
        return events.getLast();
    }

    public void clear() {
        events.clear();
    }
}
