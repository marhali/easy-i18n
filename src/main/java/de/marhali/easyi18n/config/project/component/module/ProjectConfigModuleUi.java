package de.marhali.easyi18n.config.project.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.ConfigComponent;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

import java.util.List;

/**
 * @author marhali
 */
public class ProjectConfigModuleUi extends BaseProjectConfigModuleUi {

    private final List<BaseProjectConfigModuleUi> components;

    public ProjectConfigModuleUi(Project project, ProjectConfigModule state) {
        super(project);

        this.state = state;

        this.components = List.of(
            // Register every child component here
            new ProjectConfigModuleResourceUi(project),
            new ProjectConfigModuleEditorUi(project)
        );
    }

    @Override
    public void buildComponent(FormBuilder builder) {
        for (BaseProjectConfigModuleUi component : components) {
            component.buildComponent(builder);
            builder.addVerticalGap(12);
        }
    }

    @Override
    public boolean isModified() {
        return components.stream().anyMatch(ConfigComponent::isModified);
    }

    @Override
    public void applyChangesToState() {
        for (BaseProjectConfigModuleUi component : components) {
            component.applyChangesToState();
        }
    }

    @Override
    public void applyStateToComponent(ProjectConfigModule state) {
        super.applyStateToComponent(state);

        for (BaseProjectConfigModuleUi component : components) {
            component.applyStateToComponent(state);
        }
    }
}
