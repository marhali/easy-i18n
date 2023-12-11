package de.marhali.easyi18n.util;

import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettingsState;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Objects;

/**
 * Utilities for translations
 * @author marhali
 */
public class TranslationUtil {
    /**
     * Check whether a given translation has duplicated values.
     * @param translation The translation to check
     * @param data Translation data cache
     * @return true if duplicates were found otherwise false
     */
    public static boolean hasDuplicates(@NotNull Translation translation, @NotNull TranslationData data) {
        assert translation.getValue() != null;
        Collection<String> contents = translation.getValue().getLocaleContents();

        for (KeyPath key : data.getFullKeys()) {
            if(translation.getKey().equals(key)) { // Only consider other translations
                continue;
            }

            for (String localeContent : Objects.requireNonNull(data.getTranslation(key)).getLocaleContents()) {
                if(contents.contains(localeContent)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check whether a given translation has missing locale values.
     * @param value The translation to check
     * @param data Translation data cache
     * @return true if missing values were found otherwise false
     */
    public static boolean isIncomplete(@NotNull TranslationValue value, @NotNull TranslationData data) {
        return value.getLocaleContents().size() != data.getLocales().size()
                || value.getLocaleContents().stream().anyMatch(String::isEmpty);
    }

    /**
     * Check whether a given translation falls under a specified search query.
     * @param settings Project specific settings
     * @param translation The translation to check
     * @param searchQuery Full-text search term
     * @return true if translations is applicable otherwise false
     */
    public static boolean isSearched(@NotNull ProjectSettingsState settings,
                                     @NotNull Translation translation, @NotNull String searchQuery) {

        String concatKey = new KeyPathConverter(settings).toString(translation.getKey()).toLowerCase();

        if(searchQuery.contains(concatKey) || concatKey.contains(searchQuery)) {
            return true;
        }

        assert translation.getValue() != null;
        for (String localeContent : translation.getValue().getLocaleContents()) {
            if(localeContent.toLowerCase().contains(searchQuery)) {
                return true;
            }
        }

        return false;
    }
}