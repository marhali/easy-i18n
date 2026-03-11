package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.SynchronousQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Query to retrieve all translation entry previews for the given module.
 *
 * @param moduleId Module identifier
 *
 * @author marhali
 */
public record EditorElementSuggestionsQuery(
    @NotNull ModuleId moduleId,
    @NotNull EditorElement editorElement
    ) implements SynchronousQuery<Optional<List<I18nEntryPreview>>> {
}
