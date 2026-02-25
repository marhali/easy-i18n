package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.RemoveI18nRecordsCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.EnsurePersistService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for {@link RemoveI18nRecordsCommand}.
 *
 * @author marhali
 */
public class RemoveI18nRecordsCommandHandler implements CommandHandler<RemoveI18nRecordsCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull EnsurePersistService ensurePersistService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public RemoveI18nRecordsCommandHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull EnsurePersistService ensurePersistService, @NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.ensureLoadedService = ensureLoadedService;
        this.ensurePersistService = ensurePersistService;
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull RemoveI18nRecordsCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());
            for (I18nKey key : command.keys()) {
                module.removeTranslation(key);
            }
        });
        ensurePersistService.ensurePersist(command.moduleId());
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId()));
    }
}
