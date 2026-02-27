package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.AddI18nRecordCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.EnsurePersistService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command handler for {@link AddI18nRecordCommand}.
 *
 * @author marhali
 */
public class AddI18nRecordCommandHandler implements CommandHandler<AddI18nRecordCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull EnsurePersistService ensurePersistService;
    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public AddI18nRecordCommandHandler(
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
    public void handle(@NotNull AddI18nRecordCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());
        store.mutate(project -> {
            var module = project.getOrCreateModule(command.moduleId());
            var translation = module.getOrCreateTranslation(command.key());
            for (Map.Entry<@NotNull LocaleId, @NotNull I18nValue> entry : command.content().values().entrySet()) {
                translation.put(entry.getKey(), entry.getValue());
            }
        });
        ensurePersistService.ensurePersist(command.moduleId());
        domainEventPublisherPort.publish(new ModuleChanged(command.moduleId(), command.key()));
    }
}
