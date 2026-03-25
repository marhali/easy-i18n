package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a translation target.
 *
 * @param canonicalHierarchy Canonical translation hierarchy
 * @param value Target translation value
 * @param comment Optional comment
 */
public record TranslationTarget(
    @NotNull List<@NotNull String> canonicalHierarchy,
    @NotNull I18nValue value,
    @Nullable String comment
    ) implements Comparable<TranslationTarget> {
    @Override
    public int compareTo(@NotNull TranslationTarget other) {
        var iterator = canonicalHierarchy.iterator();
        var otherIterator = other.canonicalHierarchy.iterator();

        while (iterator.hasNext() && otherIterator.hasNext()) {
            int c = iterator.next().compareTo(otherIterator.next());
            if (c != 0) return c;
        }

        if (iterator.hasNext()) return 1;
        if (otherIterator.hasNext()) return -1;

        return 0;
    }
}
