package de.marhali.easyi18n.core.domain.model;

import de.marhali.easyi18n.core.domain.template.TemplateValue;
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
    /**
     * Shorthand to construct a translation file path from a built template value.
     * @param templateValue Built template value
     * @return {@link I18nPath}
     */
    public static @NotNull I18nPath fromTemplateValue(@NotNull TemplateValue templateValue) {
        return new I18nPath(templateValue.value(), templateValue.params());
    }
}
