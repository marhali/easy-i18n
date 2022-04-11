package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the set values behind a specific translation.
 * @author marhali
 */
public class TranslationValue {

    private @NotNull Map<String, String> localeValues;

    public TranslationValue() {
        this.localeValues = new HashMap<>();
    }

    public TranslationValue(@NotNull String locale, @NotNull String content) {
        this();
        localeValues.put(locale, content);
    }

    public Set<Map.Entry<String, String>> getEntries() {
        return this.localeValues.entrySet();
    }

    public Collection<String> getLocaleContents() {
        return this.localeValues.values();
    }

    public void setLocaleValues(@NotNull Map<String, String> localeValues) {
        this.localeValues = localeValues;
    }

    public @Nullable String get(@NotNull String locale) {
        return this.localeValues.get(locale);
    }

    public void put(@NotNull String locale, @NotNull String content) {
        this.localeValues.put(locale, content);
    }

    public void remove(@NotNull String locale) {
        this.localeValues.remove(locale);
    }

    public boolean containsLocale(@NotNull String locale) {
        return this.localeValues.containsKey(locale);
    }

    public int size() {
        return this.localeValues.size();
    }

    public void clear() {
        this.localeValues.clear();
    }

    @Override
    public String toString() {
        return "TranslationValue{" +
                "localeValues=" + localeValues +
                '}';
    }
}
