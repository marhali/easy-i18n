package de.marhali.easyi18n.core.application.cqrs;

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
     * @param command Command to execute
     */
    void handle(@NotNull C command);
}
