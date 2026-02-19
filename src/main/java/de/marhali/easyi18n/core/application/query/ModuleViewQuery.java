package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.application.query.view.ModuleViewOptions;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Query to retrieve a view on the specified module with applied filters.
 *
 * @param moduleId Module identifier
 * @param options
 */
public record ModuleViewQuery(
    @NotNull ModuleId moduleId,
    @NotNull ModuleViewOptions options
    ) implements Query<ModuleView> {
}
