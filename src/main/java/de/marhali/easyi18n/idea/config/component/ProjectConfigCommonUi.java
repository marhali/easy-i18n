package de.marhali.easyi18n.idea.config.component;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.LocaleIdFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigCommonUi extends ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> {

    private @Nullable JBCheckBox editorAssistance;
    private @Nullable JBCheckBox sorting;
    private @Nullable JBTextField previewLocale;

    protected ProjectConfigCommonUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Title
        builder.addComponent(new TitledSeparator(PluginBundle.message("config.project.common.title")));

        // Editor assistance
        editorAssistance = new JBCheckBox(PluginBundle.message("config.project.common.editor-assistance.label"));
        editorAssistance.setToolTipText(PluginBundle.message("config.project.common.editor-assistance.tooltip"));

        builder.addComponent(editorAssistance, 1);

        // Sorting
        sorting = new JBCheckBox(PluginBundle.message("config.project.common.sorting.label"));
        sorting.setToolTipText(PluginBundle.message("config.project.common.sorting.tooltip"));

        builder.addComponent(sorting, 1);

        // Preview locale
        previewLocale = new JBTextField(12);
        previewLocale.setToolTipText(PluginBundle.message("config.project.common.preview-locale.tooltip"));

        builder.addLabeledComponent(PluginBundle.message("config.project.common.preview-locale.label"),
            previewLocale, 1, false);
    }

    @Override
    public boolean isModified(@NotNull ProjectConfig originState) {
        Objects.requireNonNull(editorAssistance, "editorAssistance cannot be null");
        Objects.requireNonNull(sorting, "sorting cannot be null");
        Objects.requireNonNull(previewLocale, "previewLocale cannot be null");

        var equals = editorAssistance.isSelected() == originState.editorAssistance()
            && sorting.isSelected() == originState.sorting()
            && previewLocale.getText().equals(originState.previewLocale().tag());

        return !equals;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfig state) {
        Objects.requireNonNull(editorAssistance, "editorAssistance cannot be null");
        Objects.requireNonNull(sorting, "sorting cannot be null");
        Objects.requireNonNull(previewLocale, "previewLocale cannot be null");

        editorAssistance.setSelected(state.editorAssistance());
        sorting.setSelected(state.sorting());
        previewLocale.setText(state.previewLocale().tag());
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigBuilder builder) {
        Objects.requireNonNull(editorAssistance, "editorAssistance cannot be null");
        Objects.requireNonNull(sorting, "sorting cannot be null");
        Objects.requireNonNull(previewLocale, "previewLocale cannot be null");

        builder
            .editorAssistance(editorAssistance.isSelected())
            .sorting(sorting.isSelected())
            .previewLocale(LocaleIdFactory.fromInput(previewLocale.getText()));
    }
}
