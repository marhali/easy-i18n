package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Module-specific translations container.
 *
 * @param locales Set including every used language identifier for this module.
 * @param translations Map including every managed translation for this module.
 *
 * @see MutableI18nModule
 * @author marhali
 */
public record I18nModule(
    @NotNull Set<@NotNull LocaleId> locales,
    @NotNull Map<@NotNull I18nKey, @NotNull I18nContent> translations
    ) {
    /**
     * Checks whether this module has translations for a specific locale.
     * @param localeId Locale identifier
     * @return {@code true} if translations using the given locale exist, otherwise {@code false}
     */
    public boolean hasLocale(@NotNull LocaleId localeId) {
        return locales.contains(localeId);
    }

    /**
     * Checks whether translations for the given key exist or not.
     * @param key Translation key
     * @return {@code true} if module contains translations for the specified key, otherwise {@code false}
     */
    public boolean hasTranslation(@NotNull I18nKey key) {
        return translations.containsKey(key);
    }

    /**
     * Retrieves the translation content for a specific translation key.
     * @param key Translation key
     * @return {@link I18nContent} or {@code null} if the given key has no translations
     */
    public @Nullable I18nContent getTranslation(@NotNull I18nKey key) {
        return translations.get(key);
    }

    /**
     * Retrieves the translation content for a specific translation key.
     * @param key Translation key
     * @return {@link I18nContent} or throws {@link NullPointerException} if unknown
     */
    public @NotNull I18nContent getTranslationOrThrow(@NotNull I18nKey key) {
        return Objects.requireNonNull(getTranslation(key), "Module does not contain translation with key: " + key);
    }
}
