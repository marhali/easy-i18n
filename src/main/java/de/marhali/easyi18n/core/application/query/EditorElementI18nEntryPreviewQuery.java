package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.SynchronousQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query to retrieve the {@link I18nEntryPreview} for a {@link EditorElement}.
 *
 * @param editorElement Editor element
 *
 * @author marhali
 */
public record EditorElementI18nEntryPreviewQuery(
    @NotNull ModuleId moduleId,
    @NotNull EditorElement editorElement
) implements SynchronousQuery<Optional<I18nEntryPreview>> {
}
