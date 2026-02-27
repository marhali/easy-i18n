package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Tree update specification.
 *
 * @author marhali
 */
public sealed interface TreeUpdate permits TreeUpdate.Key, TreeUpdate.Value {
    /**
     * Partial key update (replace partial key element).
     * @param parentParts Parent key parts
     * @param previousPartName Previous name of the key part to replace
     * @param newPartName New name of the key part to replace
     */
    record Key(
        @NotNull List<@NotNull String> parentParts,
        @NotNull String previousPartName,
        @NotNull String newPartName
    ) implements TreeUpdate {}

    /**
     * Translation value update for a specific locale.
     * @param key Translation key
     * @param localeId Locale identifier
     * @param newValue New translation value
     */
    record Value(
        @NotNull I18nKey key,
        @NotNull LocaleId localeId,
        @NotNull String newValue
    ) implements TreeUpdate {}
}
