package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * I18n translation with associated key path (full-key).
 * @author marhali
 */
public class KeyedTranslation {

    private @NotNull String key;
    private @Nullable Translation translation;

    public KeyedTranslation(@NotNull String key, @Nullable Translation translation) {
        this.key = key;
        this.translation = translation;
    }

    public @NotNull String getKey() {
        return key;
    }

    public void setKey(@NotNull String key) {
        this.key = key;
    }

    public @Nullable Translation getTranslation() {
        return translation;
    }

    public void setTranslation(@NotNull Translation translation) {
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "KeyedTranslation{" +
                "key='" + key + '\'' +
                ", translation=" + translation +
                '}';
    }
}