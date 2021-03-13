package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents update request to delete a existing translation.
 * @author marhali
 */
public class TranslationDelete extends TranslationUpdate {
    public TranslationDelete(@NotNull KeyedTranslation translation) {
        super(translation, null);
    }
}