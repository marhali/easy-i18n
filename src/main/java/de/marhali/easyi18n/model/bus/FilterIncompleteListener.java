package de.marhali.easyi18n.model.bus;

/**
 * Single event listener.
 * @author marhali
 */
public interface FilterIncompleteListener {
    /**
     * Toggles filter of missing translations
     * @param filter True if only translations with missing values should be shown
     */
    void onFilterIncomplete(boolean filter);
}