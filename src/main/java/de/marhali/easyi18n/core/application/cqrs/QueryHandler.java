package de.marhali.easyi18n.core.application.cqrs;

import org.jetbrains.annotations.NotNull;

/**
 * Project agnostic query handler to fulfill CQRS queries.
 *
 * @param <Q> Query type
 * @param <R> Return type
 *
 * @author marhali
 */
public interface QueryHandler<Q extends Query<R>, R> {
    /**
     * Executes the provided query within the provided project.
     *
     * @param query Query to execute
     * @return Query result
     */
    @NotNull R handle(@NotNull Q query);
}
