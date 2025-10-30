package de.marhali.easyi18n.config.project;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.component.ProjectConfigUi;
import de.marhali.easyi18n.help.EasyI18nWebHelpProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ProjectConfigConfigurable implements Configurable {

    private final Project project;

    private ProjectConfigUi component;

    public ProjectConfigConfigurable(@NotNull Project project) {
        this.project = project;
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Easy I18nNext";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new ProjectConfigUi(project);

        FormBuilder builder = FormBuilder.createFormBuilder();
        component.buildComponent(builder);
        builder.addComponentFillVertically(new JPanel(), 0);

        return builder.getPanel();
    }

    @Override
    public boolean isModified() {
        Objects.requireNonNull(component);
        return component.isModified();
    }

    @Override
    public void apply() {
        Objects.requireNonNull(component);
        component.applyChangesToState();
        ProjectConfigService.forProject(project).loadState(component.getState());
    }

    @Override
    public void reset() {
        ProjectConfig currentState = ProjectConfigService.forProject(project).getState();
        component.applyStateToComponent(currentState);
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }

    @Override
    public @Nullable @NonNls String getHelpTopic() {
        return EasyI18nWebHelpProvider.Topic.DOCS.helpTopicId();
    }
}
