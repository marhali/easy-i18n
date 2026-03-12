package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Query to resolve
 *
 * @param pathCandidate Translation path candidate
 */
public record ModuleIdByEditorFilePathQuery(
    @NotNull EditorFilePath pathCandidate
) implements Query<Optional<ModuleId>> {
}
