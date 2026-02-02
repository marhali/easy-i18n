package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.ConfiguredModulesQuery;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Query handler to read the configured modules via the {@link ProjectConfigPort}.
 *
 * @author marhali
 */
public class ConfiguredModulesQueryHandler implements QueryHandler<ConfiguredModulesQuery, Set<ModuleId>> {

    private final @NotNull ProjectConfigPort projectConfigPort;

    public ConfiguredModulesQueryHandler(@NotNull ProjectConfigPort projectConfigPort) {
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public Set<ModuleId> handle(@NotNull ProjectId projectId, @NotNull ConfiguredModulesQuery query) {
        return projectConfigPort.read(projectId).modules().keySet();
    }
}
