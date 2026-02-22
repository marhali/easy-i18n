package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a value that can be parsed or build by template.
 *
 * @param value Template value string
 * @param params Extracted / used parameters for this template value
 */
public record TemplateValue(
    @NotNull String value,
    @NotNull I18nParams params
    ) {
}
