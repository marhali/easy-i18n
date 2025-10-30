package de.marhali.easyi18n.config.project;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Persists project-specific {@link ProjectConfig configuraton}.
 *
 * @author marhali
 */
@State(
        name = "EasyI18nProjectConfig",
        storages = @Storage("easy-i18n.xml")
)
public class ProjectConfigService implements PersistentStateComponent<ProjectConfig> {

    /**
     * Returns the project-specific configuration service.
     * @param project The project
     * @return ProjectConfigService
     */
    public static @NotNull ProjectConfigService forProject(@NotNull Project project) {
        return project.getService(ProjectConfigService.class);
    }

    private ProjectConfig state;

    public ProjectConfigService() {
        this.state = ProjectConfig.fromDefaultPreset();
    }

    @Override
    public @Nullable ProjectConfig getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull ProjectConfig state) {
        this.state = state;
    }
}
