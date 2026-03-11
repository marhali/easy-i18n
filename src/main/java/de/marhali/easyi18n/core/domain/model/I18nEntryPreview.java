package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple preview container for a translation entry.
 *
 * @param key Translation key
 * @param previewValue Translation value for preview locale
 *
 * @author marhali
 */
public record I18nEntryPreview(
    @NotNull I18nKey key,
    @Nullable I18nValue previewValue
) {
    /**
     * Shorthand to construct a preview container from an actual translation entry.
     * @param entry {@link I18nEntry}
     * @param previewLocale Preview locale identifier to use
     * @return {@link I18nEntryPreview}
     */
    public static @NotNull I18nEntryPreview fromEntry(@NotNull I18nEntry entry, @NotNull LocaleId previewLocale) {
        return new I18nEntryPreview(entry.key(), entry.content().values().get(previewLocale));
    }
}
