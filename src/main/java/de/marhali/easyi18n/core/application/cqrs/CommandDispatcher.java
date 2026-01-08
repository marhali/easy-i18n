package de.marhali.easyi18n.core.application.cqrs;

import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Command handler registration and command dispatching.
 *
 * @author marhali
 */
public final class CommandDispatcher {

    private final @NotNull Map<@NotNull Class<?>, @NotNull CommandHandler<?>> handlers = new HashMap<>();

    /**
     * Registers a new command handler
     *
     * @param type Command class
     * @param handler Command handler implementation
     * @param <C> Command type
     */
    public <C extends Command> void register(@NotNull Class<C> type, @NotNull CommandHandler<C> handler) {
        handlers.put(type, handler);
    }

    /**
     * Executes the provided command within the project using any of the registered command handlers.
     *
     * @param projectId Project identifier
     * @param command Command to execute
     */
    @SuppressWarnings("unchecked")
    public void dispatch(@NotNull ProjectId projectId, @NotNull Command command) {
        var handler = (CommandHandler<Command>) handlers.get(command.getClass());

        if (handler == null) {
            throw new IllegalStateException("No handler registered for command with name '" + command.getClass().getSimpleName() + "'");
        }

        handler.handle(projectId, command);
    }
}
