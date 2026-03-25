package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.components.ActionLink;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.core.domain.config.preset.ProjectConfigModulePreset;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
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
public class ProjectConfigModulePresetUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private final @NotNull Consumer<ProjectConfigModulePreset> onApplyPreset;
    private final @NotNull Runnable onSuggestPreset;
    private boolean suppressEvents;

    private @Nullable ComboBox<ProjectConfigModulePreset> preset;

    protected ProjectConfigModulePresetUi(
        @NotNull Project project,
        @NotNull Consumer<ProjectConfigModulePreset> onApplyPreset,
        @NotNull Runnable onSuggestPreset
    ) {
        super(project);

        this.onApplyPreset = onApplyPreset;
        this.onSuggestPreset = onSuggestPreset;
        this.suppressEvents = false;
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Preset
        preset = new ComboBox<>(ProjectConfigModulePreset.values());
        preset.setToolTipText(PluginBundle.message("config.project.modules.preset.tooltip"));
        preset.setRenderer(SimpleListCellRenderer.create((label, value, index) ->
            label.setText(mapPresetToLabel(value))));

        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.preset.label"),
            preset, 1, false
        );

        // Suggest Preset
        builder.addComponent(new ActionLink(
            PluginBundle.message("config.project.modules.preset.suggest"),
            (ActionListener) (e) -> onSuggestPreset.run())
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
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        // This is a stateless child
        return false;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        Objects.requireNonNull(preset, "preset must not be null");

        // This is a stateless child

        // We reevaluate the selected preset item to
        suppressEvents = true;
        preset.setItem(ProjectConfigModulePreset.determineFromState(state));
        suppressEvents = false;
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        // This is a stateless child
    }

    private @NotNull @Nls String mapPresetToLabel(@NotNull ProjectConfigModulePreset preset) {
        return switch (preset) {
            case DEFAULT -> PluginBundle.message("config.project.modules.preset.item.default");
            case CUSTOM -> PluginBundle.message("config.project.modules.preset.item.custom");
            default -> preset.name();
        };
    }
}
