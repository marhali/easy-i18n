package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Module-specific translations container.
 *
 * @see I18nModule

 * @author marhali
 */
public final class MutableI18nModule {

    public static @NotNull MutableI18nModule empty() {
        return new MutableI18nModule(new HashSet<>(), new HashMap<>());
    }

    /**
     * Set including every used language identifier for this module.
     */
    private final @NotNull Set<@NotNull LocaleId> locales;

    /**
     * Map including every managed translation for this module.
     */
    private final @NotNull Map<@NotNull I18nKey, @NotNull MutableI18nContent> translations;

    public MutableI18nModule(
        @NotNull Set<@NotNull LocaleId> locales,
        @NotNull Map<@NotNull I18nKey, @NotNull MutableI18nContent> translations
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
     * Checks whether a specified localeId is known or not
     * @param locale Locale identifier to check
     * @return {@code true} if already tracked, otherwise {@code false}
     */
    public boolean hasLocale(@NotNull LocaleId locale) {
        return locales.contains(locale);
    }

    /**
     * Adds the provided localeId to the list of known translation targets.
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
     * @return {@link MutableI18nContent}, or {@code null} if unknown.
     */
    public @Nullable MutableI18nContent getTranslation(@NotNull I18nKey key) {
        return translations.get(key);
    }

    /**
     * Retrieves a specific translation. If the {@link I18nKey key} is unknown, a new record is created under the hood.
     * @param key Translation key.
     * @return {@link MutableI18nContent}
     */
    public @NotNull MutableI18nContent getOrCreateTranslation(@NotNull I18nKey key) {
        return translations.computeIfAbsent(key, (_key) -> new MutableI18nContent());
    }

    /**
     * Removes a specific translation.
     * @param key Translation key
     */
    public void removeTranslation(@NotNull I18nKey key) {
        translations.remove(key);
    }

    /**
     * Sets a specific translation.
     * @param key Translation key
     * @param content Translation content
     */
    public void setTranslation(@NotNull I18nKey key, @NotNull MutableI18nContent content) {
        translations.put(key, content);
    }

    /**
     * Retrieves a list of all known translation keys.
     * @return Set of translation keys
     */
    public @NotNull Set<I18nKey> getTranslationKeys() {
        return translations.keySet();
    }

    /**
     * Resets the module state.
     */
    public void clear() {
        this.locales.clear();
        this.translations.clear();
    }

    public @NotNull I18nModule toSnapshot() {
        return new I18nModule(new HashSet<>(locales), translations.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().toSnapshot(),
                (contentA, contentB) -> contentA
            )));
    }

    @Override
    public String toString() {
        return "MutableI18nModule{" +
            "locales=" + locales +
            ", translations=" + translations +
            '}';
    }
}
