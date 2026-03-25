package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.UpdateI18nValueCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.EnsurePersistService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for Update {@link UpdateI18nValueCommand}.
 *
 * @author marhali
 */
public class UpdateI18nValueCommandHandler implements CommandHandler<UpdateI18nValueCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull EnsurePersistService ensurePersistService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public UpdateI18nValueCommandHandler(
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
    public void handle(@NotNull UpdateI18nValueCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());

        store.mutate((project) -> {
            var module = project.getOrCreateModule(command.moduleId());

            var content = module.getTranslation(command.key());

            if (content == null) {
                throw new IllegalStateException("Translation has no content");
            }

            content.put(command.localeId(), command.newValue());
        });
        ensurePersistService.ensurePersist(command.moduleId());
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId(), command.key()));
    }
}
