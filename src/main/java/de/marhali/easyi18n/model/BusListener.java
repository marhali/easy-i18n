package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for communication of changes for participants of the data bus.
 * @author marhali
 */
public interface BusListener {
    /**
     * Update the translations based on the supplied data.
     * @param data Updated translations
     */
    void onUpdateData(@NotNull TranslationData data);

    /**
     * Move the specified translation key (full-key) into focus.
     * @param key Absolute translation key
     */
    void onFocusKey(@Nullable String key);

    /**
     * Filter the displayed data according to the search query. Supply 'null' to return to the normal state.
     * The keys and the content itself should be considered.
     * @param query Filter key or content
     */
    void onSearchQuery(@Nullable String query);
}