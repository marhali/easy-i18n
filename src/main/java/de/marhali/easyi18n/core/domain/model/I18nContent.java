package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Content of a single translation. Usually identified by {@link I18nKey}.
 *
 * @param values Translation values mapped by locale
 * @param comment Optional description
 *
 * @author marhali
 */
public record I18nContent(
    @NotNull Map<@NotNull LocaleId, @NotNull I18nValue> values,
    @Nullable String comment
) {
    public boolean hasComment() {
        return comment != null;
    }
}
