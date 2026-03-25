package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Query to retrieve all language targets for a specific module.
 *
 * @param moduleId Module identifier
 */
public record ModuleLocalesQuery(
    @NotNull ModuleId moduleId
    ) implements Query<Set<LocaleId>> {
}
