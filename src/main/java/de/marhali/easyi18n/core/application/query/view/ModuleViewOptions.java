package de.marhali.easyi18n.core.application.query.view;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Translation query filter options.
 *
 * @param type Module view type
 * @param filterBySearchQuery Filter translations by search query
 * @param filterByMissingValues Filter translations with missing i18n values
 * @param filterByDuplicateValues Filter translations with duplicate i18n values
 * @param filterByMissingComments Filter translations with missing comment
 */
public record ModuleViewOptions(
    @NotNull ModuleViewType type,
    @Nullable String filterBySearchQuery,
    @NotNull Boolean filterByMissingValues,
    @NotNull Boolean filterByDuplicateValues,
    @NotNull Boolean filterByMissingComments
) {
}
