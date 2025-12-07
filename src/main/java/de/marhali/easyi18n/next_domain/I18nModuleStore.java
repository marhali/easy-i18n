package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Module-wide (subset within a project) store of all translations.
 *
 * @author marhali
 */
public class I18nModuleStore {

    /**
     * Set including every used language identifier for this module.
     */
    @NotNull
    private final Set<String> locales;

    /**
     * Map including every managed translation by key for this module.
     */
    @NotNull
    private final Map<I18nKey, I18nValue> byKey;

    protected I18nModuleStore(@NotNull Set<String> locales, @NotNull Map<I18nKey, I18nValue> byKey) {
        this.locales = locales;
        this.byKey = byKey;
    }

    /**
     * Retrieves a set of all used locales in this module.
     * @return Set of locales
     */
    public @NotNull Set<String> getLocales() {
        return locales;
    }

    /**
     * Checks whether a specified locale is known or not
     * @param locale Locale identifier to check
     * @return {@code true} if already tracked, otherwise {@code false}
     */
    public boolean hasLocale(@NotNull String locale) {
        return locales.contains(locale);
    }

    /**
     * Adds the provided locale to the list of known translation targets.
     * @param locale Locale identifier
     */
    public void addLocale(@NotNull String locale) {
        this.locales.add(locale);
    }

    public I18nValue getOrCreateTranslation(@NotNull I18nKey key) {
        return this.byKey.computeIfAbsent(key, (_key) -> new I18nValue());
    }

    public boolean hasTranslation(@NotNull I18nKey key) {
        return this.byKey.containsKey(key);
    }

    public @Nullable I18nValue getTranslation(@NotNull I18nKey key) {
        return this.byKey.get(key);
    }

    public @NotNull Set<I18nKey> getKeys() {
        return this.byKey.keySet();
    }

    @Override
    public String toString() {
        return "I18nModuleStore{" +
            "locales=" + locales +
            ", byKey=" + byKey +
            '}';
    }
}
