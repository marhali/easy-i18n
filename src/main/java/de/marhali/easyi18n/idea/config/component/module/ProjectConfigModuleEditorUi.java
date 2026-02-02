package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.KeyNamingConvention;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigModuleEditorUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private @Nullable JBTextField defaultNamespace;
    private @Nullable JBTextField i18nTemplate;
    private @Nullable ComboBox<KeyNamingConvention> keyNamingConvention;

    protected ProjectConfigModuleEditorUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Title
        builder.addComponent(new TitledSeparator(PluginBundle.message("config.project.modules.item.editor.title")));

        // Default namespace
        defaultNamespace = new JBTextField();
        defaultNamespace.setToolTipText(PluginBundle.message("config.project.modules.item.default-namespace.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.default-namespace.label"),
            defaultNamespace,1, false);

        // I18n template
        i18nTemplate = new JBTextField();
        i18nTemplate.setToolTipText(PluginBundle.message("config.project.modules.item.i18n-template.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.i18n-template.label"),
            i18nTemplate, 1, false);

        // Key naming convention
        keyNamingConvention = new ComboBox<>(KeyNamingConvention.values());
        keyNamingConvention.setToolTipText(PluginBundle.message("config.project.modules.item.key-naming-convention.tooltip"));
        keyNamingConvention.setRenderer(SimpleListCellRenderer.create((label, value, index) -> label.setText(mapKeyNamingConventionToLabel(value))));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.key-naming-convention.label"),
            keyNamingConvention, 1, false);
    }

    @Override
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        Objects.requireNonNull(defaultNamespace, "defaultNamespace cannot be null");
        Objects.requireNonNull(i18nTemplate, "i18nTemplate cannot be null");
        Objects.requireNonNull(keyNamingConvention, "keyNamingConvention cannot be null");

        var equals = defaultNamespace.getText().equals(originState.defaultNamespace())
            && i18nTemplate.getText().equals(originState.i18nTemplate())
            && keyNamingConvention.getItem().equals(originState.keyNamingConvention());

        return !equals;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        Objects.requireNonNull(defaultNamespace, "defaultNamespace cannot be null");
        Objects.requireNonNull(i18nTemplate, "i18nTemplate cannot be null");
        Objects.requireNonNull(keyNamingConvention, "keyNamingConvention cannot be null");

        defaultNamespace.setText(state.defaultNamespace());
        i18nTemplate.setText(state.i18nTemplate());
        keyNamingConvention.setItem(state.keyNamingConvention());
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        Objects.requireNonNull(defaultNamespace, "defaultNamespace cannot be null");
        Objects.requireNonNull(i18nTemplate, "i18nTemplate cannot be null");
        Objects.requireNonNull(keyNamingConvention, "keyNamingConvention cannot be null");

        builder
            .defaultNamespace(defaultNamespace.getText())
            .i18nTemplate(i18nTemplate.getText())
            .keyNamingConvention(keyNamingConvention.getItem());
    }

    private @NotNull @Nls String mapKeyNamingConventionToLabel(@NotNull KeyNamingConvention keyNamingConvention) {
        return switch (keyNamingConvention) {
            case CAMEL_CASE -> PluginBundle.message("config.project.modules.item.key-naming-convention.camel_case");
            case PASCAL_CASE -> PluginBundle.message("config.project.modules.item.key-naming-convention.pascal_case");
            case SNAKE_CASE -> PluginBundle.message("config.project.modules.item.key-naming-convention.snake_case");
            case SNAKE_CASE_UPPERCASE -> PluginBundle.message("config.project.modules.item.key-naming-convention.snake_case_uppercase");
        };
    }
}
