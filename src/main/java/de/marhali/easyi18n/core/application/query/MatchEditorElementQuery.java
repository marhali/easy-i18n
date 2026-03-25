package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import org.jetbrains.annotations.NotNull;

/**
 * Query to resolve if a given editor element can be matched against the user-defined rules.
 *
 * @param moduleId Module identifier
 * @param editorElement Editor element
 *
 * @author marhali
 */
public record MatchEditorElementQuery(
    @NotNull ModuleId moduleId,
    @NotNull EditorElement editorElement
) implements Query<Boolean> {
}
