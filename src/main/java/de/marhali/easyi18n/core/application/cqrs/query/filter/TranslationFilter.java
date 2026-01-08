package de.marhali.easyi18n.core.application.cqrs.query.filter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Translation query filter options.
 *
 * @param searchQuery Filter translations by search query
 * @param missingI18nValue Filter translations with missing i18n values
 * @param missingI18nComment Filter translations with missing comment
 * @param duplicateI18nValue Filter translations with duplicate i18n values
 */
public record TranslationFilter(
    @Nullable String searchQuery,
    @NotNull Boolean missingI18nValue,
    @NotNull Boolean missingI18nComment,
    @NotNull Boolean duplicateI18nValue
) {
}
