package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a path to a translation resource.
 *
 * @param canonical Canonical file path
 * @param params File path parameters
 *
 * @author marhali
 */
public record I18nPath(
    @NotNull String canonical,
    @NotNull I18nParams params
) {
}
