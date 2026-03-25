package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Associates a {@link I18nPath} with a module.
 * @param moduleId Module identifier
 * @param path I18n path
 *
 * @author marhali
 */
public record ModuleI18nPath(
    @NotNull ModuleId moduleId,
    @NotNull I18nPath path
) {
}
