package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Translation key candidate.
 *
 * @param canonical Canonical translation key candidate
 *
 * @author marhali
 */
public record I18nKeyCandidate(
    @NotNull String canonical
) {
    /**
     * Shorthand to construct a translation key candidate.
     * @param canonical Canonical translation key candidate
     * @return {@link I18nKeyCandidate}
     */
    public static @NotNull I18nKeyCandidate of(@NotNull String canonical) {
        return new I18nKeyCandidate(canonical);
    }
}
