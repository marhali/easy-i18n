package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * I18n translation with associated key path (full-key).
 * @author marhali
 */
public class KeyedTranslation {

    private @NotNull KeyPath key;
    private @Nullable Translation translation;

    public KeyedTranslation(@NotNull KeyPath key, @Nullable Translation translation) {
        this.key = key;
        this.translation = translation;
    }

    public KeyPath getKey() {
        return key;
    }

    public void setKey(KeyPath key) {
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