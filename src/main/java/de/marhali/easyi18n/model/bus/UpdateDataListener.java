package de.marhali.easyi18n.model.bus;

import de.marhali.easyi18n.model.TranslationData;
import org.jetbrains.annotations.NotNull;

/**
 * Single event listener.
 * @author marhali
 */
public interface UpdateDataListener {
    /**
     * Update the underlying translation data set.
     * @param data Updated translations
     */
    void onUpdateData(@NotNull TranslationData data);
}