package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfig;

/**
 * @author marhali
 */
public class ProjectConfigCommonUi extends BaseProjectConfigUi{

    private JBCheckBox editorAssistance;
    private JBCheckBox sorting;
    private JBTextField previewLocale;

    protected ProjectConfigCommonUi(Project project) {
        super(project);
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.common.title")));

        // Editor assistance
        editorAssistance = new JBCheckBox(i18n.getString("config.project.common.editor-assistance.label"));
        editorAssistance.setToolTipText(i18n.getString("config.project.common.editor-assistance.tooltip"));

        formBuilder.addComponent(editorAssistance, 1);

        // Sorting
        sorting = new JBCheckBox(i18n.getString("config.project.common.sorting.label"));
        sorting.setToolTipText(i18n.getString("config.project.common.sorting.tooltip"));

        formBuilder.addComponent(sorting, 1);

        // Preview locale
        previewLocale = new JBTextField(12);
        previewLocale.setToolTipText(i18n.getString("config.project.common.preview-locale.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.common.preview-locale.label"), previewLocale,1, false);
    }

    @Override
    public boolean isModified() {
        var equals = editorAssistance.isSelected() == state.isEditorAssistance()
            && sorting.isSelected() == state.isSorting()
            && previewLocale.getText().equals(state.getPreviewLocale());

        return !equals;
    }

    @Override
    public void applyChangesToState() {
        state.setEditorAssistance(editorAssistance.isSelected());
        state.setSorting(sorting.isSelected());
        state.setPreviewLocale(previewLocale.getText());
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);

        editorAssistance.setSelected(state.isEditorAssistance());
        sorting.setSelected(state.isSorting());
        previewLocale.setText(state.getPreviewLocale());
    }
}
