package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the set values behind a specific translation.
 * @author marhali
 */
public class TranslationValue {

    private @Nullable String description;
    private @NotNull Map<String, String> values;
    private @Nullable Object misc;

    public TranslationValue(@Nullable String description, @NotNull Map<String, String> values, @Nullable Object misc) {
        this.description = description;
        this.values = values;
        this.misc = misc;
    }

    public TranslationValue(@NotNull Map<String, String> values) {
        this(null, values, null);
    }

    public TranslationValue(@NotNull String locale, @NotNull String value) {
        this(new HashMap<>(Map.of(locale, value)));
    }

    public TranslationValue() {
        this(null, new HashMap<>(), null);
    }

    /**
     * Retrieve additional description for this translation
     * @return Description
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Override or set description for this translation
     * @param description Description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Set locale specific values.
     * @param values New values
     */
    public void setValues(@NotNull Map<String, String> values) {
        this.values = values;
    }

    public @NotNull Map<String, String> getValues() {
        return values;
    }

    /**
     * Overrides or sets a value for a specific locale.
     * @param locale Locale type
     * @param value New value
     */
    public void put(@NotNull String locale, @Nullable String value) {
        if(value == null) { // Delete operation
            values.remove(locale);
        } else {
            values.put(locale, value);
        }
    }

    public void remove(String locale) {
        values.remove(locale);
    }

    /**
     * Retrieves the associated value for a specific locale
     * @param locale Locale type
     * @return Value or null if missing
     */
    public @Nullable String get(@NotNull String locale) {
        return values.get(locale);
    }

    public boolean containsLocale(@Nullable String locale) {
        return values.containsKey(locale);
    }

    public @NotNull Collection<String> getLocaleValues() {
        return values.values();
    }

    public int size() {
        return values.size();
    }

    public void clear() {
        description = null;
        values.clear();
        misc = null;
    }

    /**
     * I18n support data
     * @return Data
     */
    public @Nullable Object getMisc() {
        return misc;
    }

    /**
     * Set or update I18n support data
     * @param misc New Data
     */
    public void setMisc(@Nullable Object misc) {
        this.misc = misc;
    }

    @Override
    public String toString() {
        return "TranslationValue{" +
                "description='" + description + '\'' +
                ", values=" + values +
                ", misc=" + misc +
                '}';
    }
}
