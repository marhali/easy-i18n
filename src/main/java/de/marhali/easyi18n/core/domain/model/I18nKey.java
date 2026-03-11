package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a hierarchical translation key within an i18n module.
 *
 * @param canonical Canonical translation key {@link String}
 *
 * @author marhali
 */
public record I18nKey(
    @NotNull String canonical
) implements Comparable<I18nKey>{
    public static @NotNull I18nKey of(@NotNull String canonical) {
        return new I18nKey(canonical);
    }

    /**
     * Checks whether this translation key is a subvariant of the given translation key.
     * @param parentKey Paren translation key
     * @return {@code true} if this translation key is a subvariant of the given key, otherwise {@code false}
     */
    public boolean isSubvariant(@NotNull I18nKey parentKey) {
        return canonical.startsWith(parentKey.canonical);
    }

    @Override
    public int compareTo(@NotNull I18nKey o) {
        return canonical.compareTo(o.canonical);
    }
}
