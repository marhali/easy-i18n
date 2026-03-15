package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.fields.ExpandableTextField;
import com.intellij.util.execution.ParametersListUtil;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.core.domain.model.I18nKeyPrefix;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author marhali
 */
public class ProjectConfigModuleEditorUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private @Nullable ExpandableTextField defaultKeyPrefixes;
    private @Nullable JBTextField editorFlavorTemplate;

    protected ProjectConfigModuleEditorUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Title
        builder.addComponent(new TitledSeparator(PluginBundle.message("config.project.modules.item.editor.title")));

        // Default key prefixes
        defaultKeyPrefixes = new ExpandableTextField(ParametersListUtil.COLON_LINE_PARSER, ParametersListUtil.COLON_LINE_JOINER);
        defaultKeyPrefixes.setColumns(0);
        defaultKeyPrefixes.setToolTipText(PluginBundle.message("config.project.modules.item.default-key-prefixes.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.default-key-prefixes.label"),
            defaultKeyPrefixes,1, false);

        // I18n template
        editorFlavorTemplate = new JBTextField();
        editorFlavorTemplate.setToolTipText(PluginBundle.message("config.project.modules.item.i18n-template.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.i18n-template.label"),
            editorFlavorTemplate, 1, false);
    }

    @Override
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        Objects.requireNonNull(defaultKeyPrefixes, "defaultKeyPrefixes cannot be null");
        Objects.requireNonNull(editorFlavorTemplate, "editorFlavorTemplate cannot be null");

        var equals = defaultKeyPrefixesFromInput(defaultKeyPrefixes.getText()).equals(originState.defaultKeyPrefixes())
            && editorFlavorTemplate.getText().equals(originState.editorFlavorTemplate());

        return !equals;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        Objects.requireNonNull(defaultKeyPrefixes, "defaultKeyPrefixes cannot be null");
        Objects.requireNonNull(editorFlavorTemplate, "editorFlavorTemplate cannot be null");

        defaultKeyPrefixes.setText(defaultKeyPrefixesToInput(state.defaultKeyPrefixes()));
        editorFlavorTemplate.setText(state.editorFlavorTemplate());
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        Objects.requireNonNull(defaultKeyPrefixes, "defaultKeyPrefixes cannot be null");
        Objects.requireNonNull(editorFlavorTemplate, "editorFlavorTemplate cannot be null");

        builder
            .defaultKeyPrefixes(defaultKeyPrefixesFromInput(defaultKeyPrefixes.getText()))
            .editorFlavorTemplate(editorFlavorTemplate.getText());
    }

    private @NotNull Set<@NotNull I18nKeyPrefix> defaultKeyPrefixesFromInput(@NotNull String input) {
        return Arrays.stream(input.split(";"))
            .filter(element -> !element.isBlank())
            .map(I18nKeyPrefix::of)
            .collect(Collectors.toSet());
    }

    private @NotNull String defaultKeyPrefixesToInput(@NotNull Set<@NotNull I18nKeyPrefix> defaultKeyPrefixes) {
        return defaultKeyPrefixes.stream()
            .map(I18nKeyPrefix::canonicalPrefix)
            .collect(Collectors.joining(";"));
    }
}
