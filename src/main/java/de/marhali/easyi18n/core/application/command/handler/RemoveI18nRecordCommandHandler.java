package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.RemoveI18nRecordCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

public class RemoveI18nRecordCommandHandler implements CommandHandler<RemoveI18nRecordCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public RemoveI18nRecordCommandHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.ensureLoadedService = ensureLoadedService;
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull RemoveI18nRecordCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());
            module.removeTranslation(command.key());
        });
        // TODO: persistence?
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId()));
    }
}
