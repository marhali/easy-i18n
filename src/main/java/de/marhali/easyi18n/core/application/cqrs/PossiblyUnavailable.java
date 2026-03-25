package de.marhali.easyi18n.core.application.cqrs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Container holding possibly unavailable results.
 *
 * @param available Indicator whether the response is available or not
 * @param result Result value
 * @param <T> Result type
 *
 * @author marhali
 */
public record PossiblyUnavailable<T>(
    boolean available,
    @Nullable T result
    ) {
    /**
     * Shorthand to construct an available result.
     * @param result Result value
     * @return {@link PossiblyUnavailable}
     * @param <T> Result type
     */
    public static @NotNull <T> PossiblyUnavailable<T> available(T result) {
        return new PossiblyUnavailable<>(true, result);
    }

    /**
     * Shorthand to construct an unavailable result.
     * @return {@link PossiblyUnavailable}
     * @param <T> Result type
     */
    public static @NotNull <T> PossiblyUnavailable<T> unavailable() {
        return new PossiblyUnavailable<>(false, null);
    }
}
