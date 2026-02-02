package de.marhali.easyi18n.idea.config.component;

import com.intellij.openapi.project.Project;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigPreset;
import de.marhali.easyi18n.idea.config.ProjectConfigService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Root component for the settings configurable. Orchestrates all sub-components.
 *
 * @author marhali
 */
public class ProjectConfigUi extends ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> {

    private final @NotNull List<ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder>> components;

    public ProjectConfigUi(@NotNull Project project) {
        super(project);

        Consumer<ProjectConfigPreset> onApplyPreset = (presetProvider) ->
            writeStateToComponent(presetProvider.applyPreset(project.getService(ProjectConfigService.class).getDomainState()));

        components = List.of(
            // Register every child component here
            new ProjectConfigIntroUi(project),
            new ProjectConfigPresetUi(project, onApplyPreset),
            new ProjectConfigCommonUi(project),
            new ProjectConfigModulesUi(project)
        );
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        for (ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> component : components) {
            component.buildComponent(builder);
            builder.addVerticalGap(24);
        }
    }

    @Override
    public boolean isModified(@NotNull ProjectConfig originState) {
        return components.stream()
            .anyMatch(component -> component.isModified(originState));
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfig state) {
        for (ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> component : components) {
            component.writeStateToComponent(state);
        }
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigBuilder builder) {
        for (ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> component : components) {
            component.readStateFromComponent(builder);
        }
    }
}
