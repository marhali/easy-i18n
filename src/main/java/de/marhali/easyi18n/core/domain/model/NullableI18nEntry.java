package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Single translation entry but every field might be {@code null}.
 *
 * @param key Translation key
 * @param content Translation content
 *
 * @see I18nEntry
 *
 * @author marhali
 */
public record NullableI18nEntry(
    @Nullable I18nKey key,
    @Nullable I18nContent content
) {
    /**
     * Shorthand to create an empty entry.
     * @return {@link NullableI18nEntry}
     */
    public static @NotNull NullableI18nEntry empty() {
        return new NullableI18nEntry(null, null);
    }

    /**
     * Shorthand to create an nullable entry from an entry.
     * @param entry {@link I18nEntry}
     * @return {@link NullableI18nEntry}
     */
    public static @NotNull NullableI18nEntry from(@NotNull I18nEntry entry) {
        return new NullableI18nEntry(entry.key(), entry.content());
    }
}
