package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.ModuleI18nPathsChangedCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.ModuleI18nPath;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Command handler for {@link ModuleI18nPathsChangedCommand}.
 *
 * @author marhali
 */
public class ModuleI18nPathsChangedCommandHandler implements CommandHandler<ModuleI18nPathsChangedCommand> {

    private final @NotNull I18nStore store;
    private final @NotNull DomainEventPublisherPort domainEventPublisherPort;

    public ModuleI18nPathsChangedCommandHandler(@NotNull I18nStore store, @NotNull DomainEventPublisherPort domainEventPublisherPort) {
        this.store = store;
        this.domainEventPublisherPort = domainEventPublisherPort;
    }

    @Override
    public void handle(@NotNull ModuleI18nPathsChangedCommand command) {
        Set<@NotNull ModuleId> modulesToInvalidate = command.paths().stream()
            .map(ModuleI18nPath::moduleId)
            .collect(Collectors.toSet());

        store.mutate((project) -> {
            for (ModuleId moduleId : modulesToInvalidate) {
                project.clearModule(moduleId);
            }
        });

        for (ModuleId moduleId : modulesToInvalidate) {
            domainEventPublisherPort.publish(new ModuleChanged(moduleId, null));
        }
    }
}
