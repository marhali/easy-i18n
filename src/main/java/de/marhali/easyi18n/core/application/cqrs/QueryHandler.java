package de.marhali.easyi18n.core.application.cqrs;

import de.marhali.easyi18n.core.domain.model.ProjectId;
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
     * @param projectId Project identifier
     * @param query Query to execute
     * @return Query result
     */
    R handle(@NotNull ProjectId projectId, @NotNull Q query);
}
