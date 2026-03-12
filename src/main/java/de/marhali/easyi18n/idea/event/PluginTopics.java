package de.marhali.easyi18n.idea.event;

import com.intellij.util.messages.Topic;
import de.marhali.easyi18n.core.domain.event.DomainEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin related message topics that are placed on IntelliJ's {@link com.intellij.util.messages.MessageBus}.
 *
 * @author marhali
 */
public interface PluginTopics {
    Topic<DomainListener> DOMAIN_EVENTS = Topic.create("Domain Events", DomainListener.class);

    @FunctionalInterface
    interface DomainListener {
        void onDomainEvent(@NotNull DomainEvent event);
    }
}
