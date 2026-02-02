package de.marhali.easyi18n.idea.wiring;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.application.query.ConfiguredModulesQuery;
import de.marhali.easyi18n.core.application.query.handler.ConfiguredModulesQueryHandler;
import de.marhali.easyi18n.core.ports.DomainEventPublisherPort;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import de.marhali.easyi18n.idea.config.ProjectConfigAdapter;
import de.marhali.easyi18n.idea.event.DomainEventPublisherAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * Cheap way of dependency injection to build a project-specific {@link I18nApplication}.
 *
 * @author marhali
 */
public final class CoreWiring {

    private CoreWiring() {}

    public static @NotNull I18nApplication create(@NotNull Project project) {
        // Adapters
        ProjectConfigPort projectConfigPort = new ProjectConfigAdapter(project);
        DomainEventPublisherPort domainEventPublisherPort = new DomainEventPublisherAdapter(project);

        var commands = new CommandDispatcher();

        // Register commands here

        var queries = new QueryDispatcher();

        // Register queries here
        queries.register(ConfiguredModulesQuery.class, new ConfiguredModulesQueryHandler(projectConfigPort));

        return new I18nApplication(commands, queries);
    }
}
