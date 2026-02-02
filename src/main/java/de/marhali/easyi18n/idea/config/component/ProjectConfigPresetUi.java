package de.marhali.easyi18n.idea.config.component;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigPreset;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigPresetUi extends ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> {

    private final @NotNull Consumer<ProjectConfigPreset> onApplyPreset;
    private boolean suppressEvents;

    private @Nullable ComboBox<ProjectConfigPreset> preset;

    protected ProjectConfigPresetUi(@NotNull Project project, @NotNull Consumer<ProjectConfigPreset> onApplyPreset) {
        super(project);

        this.onApplyPreset = onApplyPreset;
        this.suppressEvents = false;
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Preset
        preset = new ComboBox<>(ProjectConfigPreset.values());
        preset.setToolTipText(PluginBundle.message("config.project.preset.tooltip"));
        preset.setRenderer(SimpleListCellRenderer.create((label, value, index) ->
            label.setText(mapPresetToLabel(value))));

        builder.addLabeledComponent(
            PluginBundle.message("config.project.preset.label"),
            preset, 1, false
        );

        // Listener

        // preset changed -> Apply preset state if events are not suppressed
        preset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!suppressEvents) {
                    onApplyPreset.accept(preset.getItem());
                }
            }
        });
    }

    @Override
    public boolean isModified(@NotNull ProjectConfig originState) {
        // This is a stateless child
        return false;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfig state) {
        Objects.requireNonNull(preset, "preset must not be null");

        // This is a stateless child

        // We reevaluate the selected preset item to
        suppressEvents = true;
        preset.setItem(ProjectConfigPreset.determineFromState(state));
        suppressEvents = false;
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigBuilder builder) {
        // This is a stateless child
    }

    private @NotNull @Nls String mapPresetToLabel(@NotNull ProjectConfigPreset preset) {
        return switch (preset) {
            case DEFAULT -> PluginBundle.message("config.project.preset.item.default");
            case CUSTOM -> PluginBundle.message("config.project.preset.item.custom");
            case MONOREPO -> PluginBundle.message("config.project.preset.item.monorepo");
        };
    }
}
