package de.marhali.easyi18n.model.bus;

import org.jetbrains.annotations.Nullable;

/**
 * Single event listener.
 * @author marhali
 */
public interface FocusKeyListener {
    /**
     * Move the specified translation key (full-key) into focus.
     * @param key Absolute translation key
     */
    void onFocusKey(@Nullable String key);
}