package de.marhali.easyi18n.model;

import java.util.Map;

/**
 * Translated messages for a dedicated key.
 * @author marhali
 */
@Deprecated // Might be deprecated
public class KeyedTranslation {

    private String key;
    private Map<String, String> translations;

    public KeyedTranslation(String key, Map<String, String> translations) {
        this.key = key;
        this.translations = translations;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    @Override
    public String toString() {
        return "KeyedTranslation{" +
                "key='" + key + '\'' +
                ", translations=" + translations +
                '}';
    }
}