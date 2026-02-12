package de.marhali.easyi18n.idea.event;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.domain.event.DomainEvent;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter to connect the {@link DomainEventPublisherPort} with IntelliJ's {@link com.intellij.util.messages.MessageBus}.
 *
 * @author marhali
 */
public class DomainEventPublisherAdapter implements DomainEventPublisherPort {

    private final @NotNull Project project;

    public DomainEventPublisherAdapter(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public void publish(@NotNull DomainEvent event) {
        project.getMessageBus().syncPublisher(PluginTopics.DOMAIN_EVENTS).onDomainEvent(event);
    }
}
