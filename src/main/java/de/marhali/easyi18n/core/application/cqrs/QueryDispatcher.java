package de.marhali.easyi18n.core.application.cqrs;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Query handler registration and query dispatching.
 *
 * @author marhali
 */
public class QueryDispatcher {

    private final @NotNull Map<@NotNull Class<?>, @NotNull QueryHandler<?, ?>> handlers = new HashMap<>();

    /**
     * Registers a new query handler
     *
     * @param type Query class
     * @param handler Query handler implementation
     * @param <Q> Query type
     * @param <R> Return type
     */
    public <Q extends Query<R>, R> void register(@NotNull Class<Q> type, @NotNull QueryHandler<Q, R> handler) {
        handlers.put(type, handler);
    }

    /**
     * Executes the provided command within the project using any of the registered query handlers.
     *
     * @param query Query to execute
     * @return Query result
     * @param <R> Return type
     */
    @SuppressWarnings("unchecked")
    public <R> @NotNull R dispatch(@NotNull Query<R> query) {
        var handler = (QueryHandler<Query<R>, R>) handlers.get(query.getClass());

        if (handler == null) {
            throw new IllegalStateException("No handler registered for query with name '" + query.getClass().getSimpleName() + "'");
        }

        return handler.handle(query);
    }
}
