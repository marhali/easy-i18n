package de.marhali.easyi18n.model.bus;

/**
 * Single event listener
 * @see #onFilterDuplicate(boolean)
 * @author marhali
 */
public interface FilterDuplicateListener {
    /**
     * Toggles filter of duplicated translation values
     * @param filter True if only translations with duplicates values should be shown
     */
    void onFilterDuplicate(boolean filter);
}
