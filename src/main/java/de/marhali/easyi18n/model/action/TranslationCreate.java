package de.marhali.easyi18n.model.action;

import de.marhali.easyi18n.model.Translation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents update request to create a new translation.
 * @author marhali
 */
public class TranslationCreate extends TranslationUpdate {
    public TranslationCreate(@NotNull Translation translation) {
        super(null, translation);
    }
}