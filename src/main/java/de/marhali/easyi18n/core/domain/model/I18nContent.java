package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Content of a translation.
 *
 * @param values Translation values mapped by locale identifier.
 * @param comment Optional description.
 *
 * @see MutableI18nContent
 *
 * @author marhali
 */
public record I18nContent(
    @NotNull Map<@NotNull LocaleId, @NotNull I18nValue> values,
    @Nullable String comment
    ) {
}
