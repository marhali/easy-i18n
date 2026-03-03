package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.ReloadCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ProjectReloaded;
import de.marhali.easyi18n.core.domain.model.MutableI18nProject;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

public class ReloadCommandHandler implements CommandHandler<ReloadCommand> {

    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public ReloadCommandHandler(@NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull ReloadCommand command) {
        store.mutate(MutableI18nProject::clearAll);
        domainEventPublisherPort.publish(new ProjectReloaded());
    }
}
