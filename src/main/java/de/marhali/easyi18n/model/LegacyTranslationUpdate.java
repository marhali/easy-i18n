package de.marhali.easyi18n.model;

import org.jetbrains.annotations.Nullable;

/**
 * Represents an update for a translated I18n-Key. Supports key creation, manipulation and deletion.
 * @author marhali
 */
@Deprecated
public class LegacyTranslationUpdate {

    private final @Nullable LegacyKeyedTranslation origin;
    private final @Nullable LegacyKeyedTranslation change;

    public LegacyTranslationUpdate(@Nullable LegacyKeyedTranslation origin, @Nullable LegacyKeyedTranslation change) {
        this.origin = origin;
        this.change = change;
    }

    public LegacyKeyedTranslation getOrigin() {
        return origin;
    }

    public LegacyKeyedTranslation getChange() {
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
