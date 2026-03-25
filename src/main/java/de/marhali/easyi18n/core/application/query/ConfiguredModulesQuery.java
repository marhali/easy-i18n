package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.ModuleId;

import java.util.Set;

/**
 * Query to retrieve all configured modules within the project.
 *
 * @author marhali
 */
public record ConfiguredModulesQuery() implements Query<Set<ModuleId>> {
}
