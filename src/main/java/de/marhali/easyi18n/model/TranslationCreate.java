package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents update request to create a new translation.
 * @author marhali
 */
public class TranslationCreate extends TranslationUpdate {
    public TranslationCreate(@NotNull LegacyKeyedTranslation translation) {
        super(null, translation);
    }
}