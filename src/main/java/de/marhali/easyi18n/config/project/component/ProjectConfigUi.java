package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.ConfigComponent;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.preset.ProjectConfigPreset;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author marhali
 */
public class ProjectConfigUi extends BaseProjectConfigUi {

    private final List<BaseProjectConfigUi> components;

    public  ProjectConfigUi(Project project) {
        super(project);

        Consumer<ProjectConfigPreset> applyPreset = (preset) ->
            applyStateToComponent(preset.applyPreset(state));

        this.components = List.of(
            // Register every child component here
            new ProjectConfigIntroUi(project),
            new ProjectConfigPresetUi(project, applyPreset),
            new ProjectConfigCommonUi(project),
            new ProjectConfigModulesUi(project)
        );
    }

    @Override
    public void buildComponent(FormBuilder builder) {
        for (BaseProjectConfigUi component : components) {
            component.buildComponent(builder);
            builder.addVerticalGap(24);
        }
    }

    @Override
    public boolean isModified() {
        return components.stream().anyMatch(ConfigComponent::isModified);
    }

    @Override
    public void applyChangesToState() {
        for (BaseProjectConfigUi component : components) {
            component.applyChangesToState();
        }
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);

        for (BaseProjectConfigUi component : components) {
            component.applyStateToComponent(state);
        }
    }
}
