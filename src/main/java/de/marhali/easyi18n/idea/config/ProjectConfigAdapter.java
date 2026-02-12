package de.marhali.easyi18n.idea.config;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

/**
 * Adapter for reading the domain-level {@link ProjectConfig} from the {@link ProjectConfigService}.
 *
 * @author marhali
 */
public class ProjectConfigAdapter implements ProjectConfigPort {

    private final @NotNull Project project;

    public ProjectConfigAdapter(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public @NotNull ProjectConfig read() {
        var projectConfigService = project.getService(ProjectConfigService.class);
        return projectConfigService.getDomainState();
    }
}
