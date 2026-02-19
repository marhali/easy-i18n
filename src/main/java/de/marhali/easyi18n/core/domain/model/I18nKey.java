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
) {
    public static @NotNull I18nKey of(@NotNull String canonical) {
        return new I18nKey(canonical);
    }
}
