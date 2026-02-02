package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import de.marhali.easyi18n.idea.wiring.CoreWiring;
import org.jetbrains.annotations.NotNull;

/**
 * Project-specific service which holds a reference to the domain-level {@link I18nApplication}.
 * Provides convenience methods to process commands and queries.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
public final class I18nProjectService {

    private final @NotNull ProjectId projectId;
    private final @NotNull I18nApplication application;

    public I18nProjectService(@NotNull Project project) {
        this.projectId = ProjectIdFactory.from(project);
        this.application = CoreWiring.create(project);
    }

    public void command(@NotNull Command command) {
        application.command(projectId, command);
    }

    public <R> @NotNull R query(@NotNull Query<R> query) {
        return application.query(projectId, query);
    }

    public @NotNull ProjectId getProjectId() {
        return projectId;
    }
}
