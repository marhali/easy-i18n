package de.marhali.easyi18n.config.project.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.KeyNamingConvention;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

import javax.swing.*;
import java.awt.*;

/**
 * @author marhali
 */
public class ProjectConfigModuleEditorUi extends BaseProjectConfigModuleUi {

    private JBTextField defaultNamespace;
    private JBTextField i18nTemplate;
    private ComboBox<KeyNamingConvention> keyNamingConvention;

    protected ProjectConfigModuleEditorUi(Project project) {
        super(project);
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.modules.item.editor.title")));

        // Default namespace
        defaultNamespace = new JBTextField();
        defaultNamespace.setToolTipText(i18n.getString("config.project.modules.item.default-namespace.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.default-namespace.label"), defaultNamespace, 1, false);

        // I18n template
        i18nTemplate = new JBTextField();
        i18nTemplate.setToolTipText(i18n.getString("config.project.modules.item.i18n-template.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.i18n-template.label"), i18nTemplate, 1, false);

        // Key naming convention
        keyNamingConvention = new ComboBox<>(KeyNamingConvention.values());
        keyNamingConvention.setToolTipText(i18n.getString("config.project.modules.item.key-naming-convention.tooltip"));
        keyNamingConvention.setRenderer(SimpleListCellRenderer.create((label, value, index) -> label.setText(value.getDisplayName())));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.key-naming-convention.label"), keyNamingConvention, 1, false);
    }

    @Override
    public boolean isModified() {
        var equals = defaultNamespace.getText().equals(state.getDefaultNamespace())
            && i18nTemplate.getText().equals(state.getI18nTemplate())
            && keyNamingConvention.getItem().equals(state.getKeyNamingConvention());

        return !equals;
    }

    @Override
    public void applyChangesToState() {
        state.setDefaultNamespace(defaultNamespace.getText());
        state.setI18nTemplate(i18nTemplate.getText());
        state.setKeyNamingConvention(keyNamingConvention.getItem());
    }

    @Override
    public void applyStateToComponent(ProjectConfigModule state) {
        super.applyStateToComponent(state);

        defaultNamespace.setText(state.getDefaultNamespace());
        i18nTemplate.setText(state.getI18nTemplate());
        keyNamingConvention.setItem(state.getKeyNamingConvention());
    }
}
