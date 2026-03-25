package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to ensure that the provided {@link ModuleId module} is loaded.
 *
 * @param moduleId Module identifier
 *
 * @author marhali
 */
public record EnsureModuleLoadedCommand(
    @NotNull ModuleId moduleId
    ) implements Command {
}
