package de.marhali.easyi18n.model;

import java.util.HashMap;

/**
 * Represents all translations for an element. The assignment to an element is done in the using class.
 * This class contains only the translations for this unspecific element.
 * @author marhali
 */
@Deprecated // Replaced by TranslationValue
public class Translation extends HashMap<String, String> {
    public Translation() {
        super();
    }

    public Translation(String locale, String content) {
        this();
        super.put(locale, content);
    }

    public Translation add(String locale, String content) {
        super.put(locale, content);
        return this;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}