package de.marhali.easyi18n.core.domain.template.flavor;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import org.jetbrains.annotations.NotNull;

/**
 * Translation flavor template.
 *
 * @author marhali
 */
public interface I18nFlavorTemplate {
    /**
     * Constructs a translation flavor for the given translation key.
     * @param key Translation key
     * @return Filled translation flavor
     */
    @NotNull String fromI18nKey(@NotNull I18nKey key);
}
