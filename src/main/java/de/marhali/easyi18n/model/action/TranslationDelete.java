package de.marhali.easyi18n.model.action;

import de.marhali.easyi18n.model.Translation;

import org.jetbrains.annotations.NotNull;

/**
 * Represents update request to delete a existing translation.
 * @author marhali
 */
public class TranslationDelete extends TranslationUpdate {
    public TranslationDelete(@NotNull Translation translation) {
        super(translation, null);
    }
}