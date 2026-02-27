package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.UpdateI18nKeyCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.EnsurePersistService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for {@link UpdateI18nKeyCommandHandler}.
 *
 * @author marhali
 */
public class UpdateI18nKeyCommandHandler implements CommandHandler<UpdateI18nKeyCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull EnsurePersistService ensurePersistService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public UpdateI18nKeyCommandHandler(
        @NotNull EnsureLoadedService ensureLoadedService,
        @NotNull EnsurePersistService ensurePersistService,
        @NotNull I18nStore store,
        @NotNull DomainEventPublisherPort domainEventPublisherPort
    ) {
        this.ensureLoadedService = ensureLoadedService;
        this.ensurePersistService = ensurePersistService;
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull UpdateI18nKeyCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());

            var content = module.getTranslation(command.oldKey());

            if (content == null) {
                throw new IllegalStateException("Old translation has no content");
            }

            module.removeTranslation(command.oldKey());
            module.setTranslation(command.newKey(), content);
        });
        ensurePersistService.ensurePersist(command.moduleId());
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId(), command.newKey()));
    }
}
