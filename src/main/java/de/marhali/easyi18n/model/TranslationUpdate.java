package de.marhali.easyi18n.model;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an update for a translated i18n key.
 * Supports translation creation, manipulation and deletion.
 *
 * @author marhali
 */
public class TranslationUpdate {

    private final @Nullable KeyedTranslation origin;
    private final @Nullable KeyedTranslation change;

    public TranslationUpdate(@Nullable KeyedTranslation origin, @Nullable KeyedTranslation change) {
        this.origin = origin;
        this.change = change;
    }

    public @Nullable KeyedTranslation getOrigin() {
        return origin;
    }

    public @Nullable KeyedTranslation getChange() {
        return change;
    }

    public boolean isCreation() {
        return this.origin == null;
    }

    public boolean isDeletion() {
        return this.change == null;
    }

    public boolean isKeyChange() {
        return this.origin != null && this.change != null && !this.origin.getKey().equals(this.change.getKey());
    }

    @Override
    public String toString() {
        return "TranslationUpdate{" +
                "origin=" + origin +
                ", change=" + change +
                '}';
    }
}
