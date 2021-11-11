package de.marhali.easyi18n.model.bus;

import org.jetbrains.annotations.Nullable;

/**
 * Single event listener.
 * @author marhali
 */
public interface SearchQueryListener {
    /**
     * Filter the displayed data according to the search query. Supply 'null' to return to the normal state.
     * The keys and the content itself should be considered.
     * @param query Filter key or content
     */
    void onSearchQuery(@Nullable String query);
}