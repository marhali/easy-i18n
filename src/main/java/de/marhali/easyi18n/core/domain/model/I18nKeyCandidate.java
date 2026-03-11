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
}
