package de.marhali.easyi18n.core.application.cqrs;

import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Project agnostic command handler to fulfill CQRS commands.
 *
 * @param <C> Command type
 *
 * @author marhali
 */
public interface CommandHandler<C extends Command> {
    /**
     * Executes the provided command within the provided project.
     *
     * @param projectId Project identifier
     * @param command Command to execute
     */
    void handle(@NotNull ProjectId projectId, @NotNull C command);
}
