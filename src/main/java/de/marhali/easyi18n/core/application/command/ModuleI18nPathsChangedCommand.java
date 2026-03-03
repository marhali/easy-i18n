package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.ModuleI18nPath;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Command to indicate that a set of module translation file paths have been changed externally.
 *
 * @param paths Affected paths
 *
 * @author marhali
 */
public record ModuleI18nPathsChangedCommand(
    @NotNull Set<@NotNull ModuleI18nPath> paths
    ) implements Command {
}
