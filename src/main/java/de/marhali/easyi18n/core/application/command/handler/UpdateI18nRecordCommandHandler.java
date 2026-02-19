package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.UpdateI18nRecordCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.MutableI18nContent;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

public class UpdateI18nRecordCommandHandler implements CommandHandler<UpdateI18nRecordCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public UpdateI18nRecordCommandHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.ensureLoadedService = ensureLoadedService;
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull UpdateI18nRecordCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());

            if (!command.originKey().equals(command.key())) {
                // Key has been changed - just remove the old translation
                module.removeTranslation(command.originKey());
            }

            module.setTranslation(command.key(), MutableI18nContent.fromSnapshot(command.content()));
        });
        // TODO: persistence?
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId()));
    }
}
