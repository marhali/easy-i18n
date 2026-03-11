package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query to resolve the associated {@link ModuleId} for the provided {@link EditorElement}.
 *
 * @param editorElement Editor element
 *
 * @author marhali
 */
public record EditorElementModuleQuery(
    @NotNull EditorElement editorElement
) implements Query<Optional<ModuleId>> {
}
