package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Single translation entry.
 *
 * @param key Translation key
 * @param content Translation content
 *
 * @author marhali
 */
public record I18nEntry(
    @NotNull I18nKey key,
    @NotNull I18nContent content
) {
}
