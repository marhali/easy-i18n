package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a prefix for a translation key.
 *
 * @param canonicalPrefix Canonical translation key prefix
 *
 * @author marhali
 */
public record I18nKeyPrefix(
    @NotNull String canonicalPrefix
) implements Comparable<I18nKeyPrefix> {
    public static @NotNull I18nKeyPrefix of(@NotNull String canonicalPrefix) {
        return new I18nKeyPrefix(canonicalPrefix);
    }

    /**
     * Checks whether a given translation key is prefixed by this translation key prefix.
     * @param key Translation key
     * @return {@code true} if the provided key starts with the prefix, otherwise {@code false}
     */
    public boolean isPrefixed(@NotNull I18nKey key) {
        return key.canonical().startsWith(canonicalPrefix);
    }

    public @NotNull I18nKey withoutPrefix(@NotNull I18nKey key) {
        return I18nKey.of(key.canonical().substring(canonicalPrefix.length()));
    }

    public @NotNull I18nKey withCandidate(@NotNull I18nKeyCandidate keyCandidate) {
        return I18nKey.of(canonicalPrefix + keyCandidate.canonical());
    }

    @Override
    public int compareTo(@NotNull I18nKeyPrefix o) {
        return canonicalPrefix.compareTo(o.canonicalPrefix);
    }
}
