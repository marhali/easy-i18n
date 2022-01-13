package de.marhali.easyi18n.model.bus;

import de.marhali.easyi18n.model.KeyPath;

import org.jetbrains.annotations.NotNull;

/**
 * Single event listener.
 * @author marhali
 */
public interface FocusKeyListener {
    /**
     * Move the specified translation key (full-key) into focus.
     * @param key Absolute translation key
     */
    void onFocusKey(@NotNull KeyPath key);
}