package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.application.command.EnsureModuleLoadedCommand;
import de.marhali.easyi18n.core.application.cqrs.CommandHandler;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import org.jetbrains.annotations.NotNull;

/**
 * Command handler for {@link EnsureModuleLoadedCommand}.
 *
 * @author marhali
 */
public class EnsureModuleLoadedCommandHandler implements CommandHandler<EnsureModuleLoadedCommand> {

    private final @NotNull EnsureLoadedService ensureLoadedService;

    public EnsureModuleLoadedCommandHandler(@NotNull EnsureLoadedService ensureLoadedService) {
        this.ensureLoadedService = ensureLoadedService;
    }

    @Override
    public void handle(@NotNull EnsureModuleLoadedCommand command) {
        ensureLoadedService.ensureLoaded(command.moduleId());
    }
}
