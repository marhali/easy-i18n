package de.marhali.easyi18n.model.bus;

/**
 * Single event listener.
 * @author marhali
 */
public interface FilterMissingTranslationsListener {
    /**
     * Toggles filter of missing translations
     * @param filter True if only translations with missing values should bw shown
     */
    void onFilterMissingTranslations(boolean filter);
}