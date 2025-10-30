package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.preset.ProjectConfigPreset;

import java.util.function.Consumer;

/**
 * @author marhali
 */
public class ProjectConfigPresetUi extends BaseProjectConfigUi{

    private final Consumer<ProjectConfigPreset> onApplyPreset;

    private ComboBox<ProjectConfigPreset> preset;

    private ProjectConfigPreset currentPreset;
    private ProjectConfigPreset lastPreset;

    protected ProjectConfigPresetUi(Project project, Consumer<ProjectConfigPreset> onApplyPreset) {
        super(project);

        this.onApplyPreset = onApplyPreset;
    }

    @Override
    public void buildComponent(FormBuilder builder) {
        // Preset
        preset = new ComboBox<>(ProjectConfigPreset.values());
        preset.setToolTipText(i18n.getString("config.project.preset.tooltip"));
        preset.setRenderer(SimpleListCellRenderer.create((label, value, index) -> label.setText(value.getDisplayName())));

        builder.addLabeledComponent(i18n.getString("config.project.preset.label"), preset, 1, false);

        // Listener

        // Preset changed -> Apply preset state
        preset.addActionListener(e -> {
            lastPreset = currentPreset;
            currentPreset = preset.getItem();
            onApplyPreset.accept(currentPreset);
        });
    }

    @Override
    public boolean isModified() {
        // This is a stateless child
        return !currentPreset.equals(lastPreset);
    }

    @Override
    public void applyChangesToState() {
        // This is a stateless child
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);
        // This is a stateless child
        determineCurrentPreset();
    }

    private void determineCurrentPreset() {
        if (currentPreset != null && !currentPreset.equals(ProjectConfigPreset.DEFAULT)) {
            if (state.equals(currentPreset.applyPreset(new ProjectConfig(state)))) {
                preset.setItem(currentPreset);
                return;
            }
        }

        var stateEqualsDefaultPreset = state.equals(ProjectConfigPreset.DEFAULT.applyPreset(new ProjectConfig(state)));
        currentPreset = stateEqualsDefaultPreset ? ProjectConfigPreset.DEFAULT : ProjectConfigPreset.CUSTOM;
        preset.setItem(currentPreset);
    }
}
