package de.marhali.easyi18n.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.InstanceManager;

import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * IDE settings panel for this plugin
 * @author marhali
 */
public class ProjectSettingsConfigurable implements Configurable {

    private final Project project;

    private ProjectSettingsComponent component;

    public ProjectSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return "Easy I18n";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new ProjectSettingsComponent(project);
        component.setState(ProjectSettingsService.get(project).getState());
        return component.getMainPanel();
    }

    @Override
    public boolean isModified() {
        ProjectSettingsState originState = ProjectSettingsService.get(project).getState();
        return !originState.equals(component.getState());
    }

    @Override
    public void apply() {
        ProjectSettingsService.get(project).setState(component.getState());
        InstanceManager.get(project).reload();
    }

    @Override
    public void reset() {
        component.setState(ProjectSettingsService.get(project).getState());
    }

    @Override
    public void disposeUIResources() {
        component = null;
    }
}
