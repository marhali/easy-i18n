package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Module-specific translations container.
 *
 * @author marhali
 */
public class I18nModule {

    /**
     * Set including every used language identifier for this module.
     */
    private final @NotNull Set<@NotNull LocaleId> locales;

    /**
     * Map including every managed translation for this module.
     */
    private final @NotNull Map<@NotNull I18nKey, @NotNull I18nContent> translations;

    I18nModule(
        @NotNull Set<@NotNull LocaleId> locales,
        @NotNull Map<@NotNull I18nKey, @NotNull I18nContent> translations
    ) {
        this.locales = locales;
        this.translations = translations;
    }

    /**
     * Retrieves a set of all used locales in this module.
     * @return Set of locales
     */
    public @NotNull Set<@NotNull LocaleId> getLocales() {
        return locales;
    }

    /**
     * Checks whether a specified locale is known or not
     * @param locale Locale identifier to check
     * @return {@code true} if already tracked, otherwise {@code false}
     */
    public boolean hasLocale(@NotNull LocaleId locale) {
        return locales.contains(locale);
    }

    /**
     * Adds the provided locale to the list of known translation targets.
     * @param locale Locale identifier
     */
    public void addLocale(@NotNull LocaleId locale) {
        this.locales.add(locale);
    }


    /**
     * Checks whether a specific translation is existing.
     * @param key Translation key
     * @return {@code true} if translation is tracked, otherwise {@code false}.
     */
    public boolean hasTranslation(@NotNull I18nKey key) {
        return translations.containsKey(key);
    }

    /**
     * Retrieves a specific translation.
     * @param key Translation key
     * @return {@link I18nContent}, or {@code null} if unknown.
     */
    public @Nullable I18nContent getTranslation(@NotNull I18nKey key) {
        return translations.get(key);
    }

    /**
     * Retrieves a specific translation. If the {@link I18nKey key} is unknown, a new record is created under the hood.
     * @param key Translation key.
     * @return {@link I18nContent}
     */
    public @NotNull I18nContent getOrCreateTranslation(@NotNull I18nKey key) {
        return translations.computeIfAbsent(key, (_key) -> new I18nContent());
    }

    /**
     * Retrieves a list of all known translation keys.
     * @return Set of translation keys
     */
    public @NotNull Set<I18nKey> getTranslationKeys() {
        return translations.keySet();
    }

    @Override
    public String toString() {
        return "I18nModule{" +
            "locales=" + locales +
            ", translations=" + translations +
            '}';
    }
}
