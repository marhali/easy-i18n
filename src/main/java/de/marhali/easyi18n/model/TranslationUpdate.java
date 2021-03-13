package de.marhali.easyi18n.model;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an update for a translated I18n-Key. Supports key creation, manipulation and deletion.
 * @author marhali
 */
public class TranslationUpdate {

    private final @Nullable KeyedTranslation origin;
    private final @Nullable KeyedTranslation change;

    public TranslationUpdate(@Nullable KeyedTranslation origin, @Nullable KeyedTranslation change) {
        this.origin = origin;
        this.change = change;
    }

    public KeyedTranslation getOrigin() {
        return origin;
    }

    public KeyedTranslation getChange() {
        return change;
    }

    public boolean isCreation() {
        return origin == null;
    }

    public boolean isDeletion() {
        return change == null;
    }

    public boolean isKeyChange() {
        return origin != null && change != null && !origin.getKey().equals(change.getKey());
    }

    @Override
    public String toString() {
        return "TranslationUpdate{" +
                "origin=" + origin +
                ", change=" + change +
                '}';
    }
}
