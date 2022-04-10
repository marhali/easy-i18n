package de.marhali.easyi18n.model.translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a translation with defined key and locale values.
 *
 * @author marhali
 */
public class Translation {

    private final @NotNull KeyPath key;
    private @Nullable TranslationValue value;

    /**
     * Constructs a new translation instance.
     * @param key Absolute key path
     * @param value Values to set - nullable to indicate removal
     */
    public Translation(@NotNull KeyPath key, @Nullable TranslationValue value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return Absolute key path
     */
    public @NotNull KeyPath getKey() {
        return key;
    }

    /**
     * @return values - nullable to indicate removal
     */
    public @Nullable TranslationValue getValue() {
        return value;
    }

    /**
     * @param value Values to set - nullable to indicate removal
     */
    public void setValue(@Nullable TranslationValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}