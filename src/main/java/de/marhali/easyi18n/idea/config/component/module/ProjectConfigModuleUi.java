package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author marhali
 */
public class ProjectConfigModuleUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private final @NotNull ModuleId moduleId;
    private final @NotNull List<ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder>> components;

    public ProjectConfigModuleUi(@NotNull Project project, @NotNull ModuleId moduleId) {
        super(project);

        this.moduleId = moduleId;
        this.components = List.of(
            // Register every child component here
            new ProjectConfigModuleResourceUi(project),
            new ProjectConfigModuleEditorUi(project)
        );
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        for (ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> component : components) {
            component.buildComponent(builder);
            builder.addVerticalGap(12);
        }
    }

    @Override
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        return components.stream()
            .anyMatch((component) -> component.isModified(originState));
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        for (ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> component : components) {
            component.writeStateToComponent(state);
        }
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        builder.id(moduleId);

        for (ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> component : components) {
            component.readStateFromComponent(builder);
        }
    }

    public @NotNull ModuleId getModuleId() {
        return moduleId;
    }
}
