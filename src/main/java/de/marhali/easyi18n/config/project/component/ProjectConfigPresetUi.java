package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.preset.ProjectConfigPreset;

import java.awt.event.ActionListener;
import java.util.function.Consumer;

/**
 * @author marhali
 */
public class ProjectConfigPresetUi extends BaseProjectConfigUi{

    private final Consumer<ProjectConfigPreset> onApplyPreset;

    private ComboBox<ProjectConfigPreset> preset;
    private ActionListener presetActonListener;

    private ProjectConfig previousPersistedState;

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
        // presetActionListener is also registered / unregistered via #applyStateToComponent
        presetActonListener = e -> {
            if (previousPersistedState == null) {
                // Keep a copy of last persisted state to determine if anything has changed for #isModified
                previousPersistedState = new ProjectConfig(state);
            }
            onApplyPreset.accept(preset.getItem()); // Propagate new selected preset
        };

        preset.addActionListener(presetActonListener);
    }

    @Override
    public boolean isModified() {
        // We need to provide if any preset has been applied to trigger save / revert action
        // We say its always modified is any existing previousPersistedState does not match with the current state
        return previousPersistedState != null && !state.equals(previousPersistedState);
    }

    @Override
    public void applyChangesToState() {
        // This is a stateless child
        // Reset stored previousPersistedState as the current state will be persisted now
        previousPersistedState = null;
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);

        // This is a stateless child

        if (state.equals(previousPersistedState)) {
            // If we are reverting the state to the previousPersistedState we can safely reset it
            previousPersistedState = null;
        }

        preset.removeActionListener(presetActonListener); // Unregister action listener to prevent false positives

        determineCurrentPreset();

        preset.addActionListener(presetActonListener); // Register action listener again
    }

    private void determineCurrentPreset() {
        if (previousPersistedState == null) {
            // Only determine preset if no preset has been selected manually previously
            var designatedPreset = preset.getItem() == null ? ProjectConfigPreset.DEFAULT : preset.getItem();
            var stateEqualsPreset = state.equals(designatedPreset.applyPreset(ProjectConfig.fromDefaultPreset()));

            preset.setItem(stateEqualsPreset ? designatedPreset : ProjectConfigPreset.CUSTOM);
        }
    }
}
