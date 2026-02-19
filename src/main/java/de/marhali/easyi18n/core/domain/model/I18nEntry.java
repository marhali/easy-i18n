package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Single translation entry.
 *
 * @param key Translation key
 * @param content Translation content
 *
 * @author marhali
 */
public record I18nEntry(
    @NotNull I18nKey key,
    @NotNull I18nContent content
) {
    public static @NotNull I18nEntry fromEntry(@NotNull Map.Entry<@NotNull I18nKey, @NotNull I18nContent> entry) {
        return new I18nEntry(entry.getKey(), entry.getValue());
    }
}
