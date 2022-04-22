package de.marhali.easyi18n.model.action;

import de.marhali.easyi18n.model.Translation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an update for a translated i18n key.
 * Supports translation creation, manipulation and deletion.
 *
 * @author marhali
 */
public class TranslationUpdate {

    private final @Nullable Translation origin;
    private final @Nullable Translation change;

    public TranslationUpdate(@Nullable Translation origin, @Nullable Translation change) {
        this.origin = origin;
        this.change = change;
    }

    public @Nullable Translation getOrigin() {
        return origin;
    }

    public @Nullable Translation getChange() {
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
