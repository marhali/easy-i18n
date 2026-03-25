package de.marhali.easyi18n.core.application.cqrs;

/**
 * Marker interface to indicate CQRS queries that <b>need</b> to be executed synchronously.
 *
 * @param <R> Result type
 *
 * @author marhali
 */
public interface SynchronousQuery<R> extends Query<PossiblyUnavailable<R>> {
}
