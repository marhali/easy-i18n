package de.marhali.easyi18n.util;

import de.marhali.easyi18n.model.Translation;

/**
 * Translation builder utility.
 * @author marhali
 */
public class TranslationBuilder {

    private Translation translation;

    public TranslationBuilder() {
        this.translation = new Translation();
    }

    public TranslationBuilder(String locale, String content) {
        this();
        this.translation.put(locale, content);
    }

    public TranslationBuilder add(String locale, String content) {
        this.translation.put(locale, content);
        return this;
    }

    public Translation build() {
        return this.translation;
    }
}