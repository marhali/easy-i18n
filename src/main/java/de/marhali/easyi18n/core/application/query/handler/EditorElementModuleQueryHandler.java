package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.EditorElementModuleQuery;
import de.marhali.easyi18n.core.application.service.EditorElementModuleResolver;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link EditorElementModuleQuery}.
 *
 * @author marhali
 */
public class EditorElementModuleQueryHandler
    implements QueryHandler<EditorElementModuleQuery, Optional<ModuleId>> {

    private final @NotNull EditorElementModuleResolver editorElementModuleResolver;

    public EditorElementModuleQueryHandler(@NotNull EditorElementModuleResolver editorElementModuleResolver) {
        this.editorElementModuleResolver = editorElementModuleResolver;
    }

    @Override
    public @NotNull Optional<ModuleId> handle(@NotNull EditorElementModuleQuery query) {
        ModuleId moduleId = editorElementModuleResolver.resolve(query.editorElement());
        return Optional.ofNullable(moduleId);
    }
}
