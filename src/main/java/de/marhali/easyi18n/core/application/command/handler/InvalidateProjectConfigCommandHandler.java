package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.InvalidateProjectConfigCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.CachedModuleTamplates;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ProjectConfigChanged;
import de.marhali.easyi18n.core.domain.model.MutableI18nProject;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

public class InvalidateProjectConfigCommandHandler implements CommandHandler<InvalidateProjectConfigCommand> {

    private final @NotNull I18nStore store;
    private final @NotNull CachedModuleTamplates cachedModuleTamplates;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public InvalidateProjectConfigCommandHandler(@NotNull I18nStore store, @NotNull CachedModuleTamplates cachedModuleTamplates, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.store = store;
        this.cachedModuleTamplates = cachedModuleTamplates;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull InvalidateProjectConfigCommand command) {
        store.mutate(MutableI18nProject::clear);
        cachedModuleTamplates.invalidateAll();
        domainEventPublisherPort.publish(new ProjectConfigChanged());
    }
}
