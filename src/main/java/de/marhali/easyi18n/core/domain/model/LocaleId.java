package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Language code identifier.
 *
 * @param tag Locale tag
 *
 * @author marhali
 */
public record LocaleId(
    @NotNull String tag
) implements Comparable<LocaleId> {
    @Override
    public int compareTo(@NotNull LocaleId o) {
        return this.tag.compareTo(o.tag);
    }
}
