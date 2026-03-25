package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.application.service.ModuleIdByEditorFilePathResolver;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link ModuleIdByEditorFilePathQuery}.
 *
 * @author marhali
 */
public class ModuleIdByEditorFilePathQueryHandler implements QueryHandler<ModuleIdByEditorFilePathQuery, Optional<ModuleId>> {

    private final @NotNull ModuleIdByEditorFilePathResolver moduleIdByEditorFilePathResolver;

    public ModuleIdByEditorFilePathQueryHandler(@NotNull ModuleIdByEditorFilePathResolver moduleIdByEditorFilePathResolver) {
        this.moduleIdByEditorFilePathResolver = moduleIdByEditorFilePathResolver;
    }

    @Override
    public @NotNull Optional<ModuleId> handle(@NotNull ModuleIdByEditorFilePathQuery query) {
        ModuleId moduleId = moduleIdByEditorFilePathResolver.resolve(query.pathCandidate());
        return Optional.ofNullable(moduleId);
    }
}
