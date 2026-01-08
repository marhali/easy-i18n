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
) {
}
