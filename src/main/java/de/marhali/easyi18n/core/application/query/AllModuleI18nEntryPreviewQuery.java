package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.SynchronousQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Query to retrieve a list of all {@link I18nEntryPreview} elements for the given module.
 *
 * @param moduleId Module identifier
 *
 * @author marhali
 */
public record AllModuleI18nEntryPreviewQuery(
    @NotNull ModuleId moduleId
) implements SynchronousQuery<List<I18nEntryPreview>> {
}
