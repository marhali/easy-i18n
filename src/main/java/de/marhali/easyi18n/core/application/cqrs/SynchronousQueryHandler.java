package de.marhali.easyi18n.core.application.cqrs;

/**
 * Project agnostic <b>synchronous</b> query handler to fulfill CQRS queries.
 * @param <Q> Query type
 * @param <R> Return type
 *
 * @author marhali
 */
public interface SynchronousQueryHandler<Q extends SynchronousQuery<R>, R> extends QueryHandler<Q, PossiblyUnavailable<R>> {
}
