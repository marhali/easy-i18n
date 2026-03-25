package de.marhali.easyi18n.idea.config.component.module;

import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.idea.config.component.ConfigComponent;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * @author marhali
 */
public class ProjectConfigModuleResourceUi
    extends ConfigComponent<FormBuilder, ProjectConfigModule, ProjectConfigModuleBuilder> {

    private @Nullable JBTextField pathTemplate;
    private @Nullable ComboBox<FileCodec> fileCodec;
    private @Nullable JBTextField fileTemplate;
    private @Nullable JBTextField keyTemplate;
    private @Nullable JBTextField rootDirectory;

    protected ProjectConfigModuleResourceUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        // Title
        builder.addComponent(new TitledSeparator(PluginBundle.message("config.project.modules.item.resource.title")));

        // Path template syntax
        pathTemplate = new JBTextField();
        pathTemplate.setToolTipText(PluginBundle.message("config.project.modules.item.path.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.path.label"),
            pathTemplate, 1, false);

        // File codec & template panel
        JPanel filePanel = new NonOpaquePanel(new BorderLayout());

        fileCodec = new ComboBox<>(FileCodec.values());
        fileCodec.setToolTipText(PluginBundle.message("config.project.modules.item.file.codec.tooltip"));
        fileCodec.setRenderer(SimpleListCellRenderer.create((label, value, index) -> label.setText(mapFileCodecToLabel(value))));
        filePanel.add(fileCodec, BorderLayout.WEST);

        fileTemplate = new JBTextField();
        fileTemplate.setToolTipText(PluginBundle.message("config.project.modules.item.file.template.tooltip"));
        filePanel.add(fileTemplate, BorderLayout.CENTER);

        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.file.label"),
            filePanel, 1, false);

        // Key template syntax
        keyTemplate = new JBTextField();
        keyTemplate.setToolTipText(PluginBundle.message("config.project.modules.item.key.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.key.label"),
            keyTemplate, 1, false);

        // Root directory
        rootDirectory = new JBTextField();
        rootDirectory.setToolTipText(PluginBundle.message("config.project.modules.item.dir.tooltip"));
        builder.addLabeledComponent(
            PluginBundle.message("config.project.modules.item.dir.label"),
            rootDirectory, 1, false);
    }

    @Override
    public boolean isModified(@NotNull ProjectConfigModule originState) {
        Objects.requireNonNull(pathTemplate, "pathTemplate must not be null");
        Objects.requireNonNull(fileCodec, "fileCodec must not be null");
        Objects.requireNonNull(fileTemplate, "fileTemplate must not be null");
        Objects.requireNonNull(keyTemplate, "keyTemplate must not be null");
        Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");

        var equals = expandPathMacros(pathTemplate.getText()).equals(originState.pathTemplate())
            && fileCodec.getItem().equals(originState.fileCodec())
            && fileTemplate.getText().equals(originState.fileTemplate())
            && keyTemplate.getText().equals(originState.keyTemplate())
            && expandPathMacros(rootDirectory.getText()).equals(originState.rootDirectory());

        return !equals;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfigModule state) {
        Objects.requireNonNull(pathTemplate, "pathTemplate must not be null");
        Objects.requireNonNull(fileCodec, "fileCodec must not be null");
        Objects.requireNonNull(fileTemplate, "fileTemplate must not be null");
        Objects.requireNonNull(keyTemplate, "keyTemplate must not be null");
        Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");

        pathTemplate.setText(collapsePathMacros(state.pathTemplate()));
        fileCodec.setItem(state.fileCodec());
        fileTemplate.setText(state.fileTemplate());
        keyTemplate.setText(state.keyTemplate());
        rootDirectory.setText(collapsePathMacros(state.rootDirectory()));
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigModuleBuilder builder) {
        Objects.requireNonNull(pathTemplate, "pathTemplate must not be null");
        Objects.requireNonNull(fileCodec, "fileCodec must not be null");
        Objects.requireNonNull(fileTemplate, "fileTemplate must not be null");
        Objects.requireNonNull(keyTemplate, "keyTemplate must not be null");
        Objects.requireNonNull(rootDirectory, "rootDirectory must not be null");

        builder
            .pathTemplate(expandPathMacros(pathTemplate.getText()))
            .fileCodec(fileCodec.getItem())
            .fileTemplate(fileTemplate.getText())
            .keyTemplate(keyTemplate.getText())
            .rootDirectory(expandPathMacros(rootDirectory.getText()));
    }

    private @NotNull @Nls String mapFileCodecToLabel(@NotNull FileCodec fileCodec) {
        return switch (fileCodec) {
            case JSON -> PluginBundle.message("config.project.modules.item.file.codec.json");
            case JSON5 -> PluginBundle.message("config.project.modules.item.file.codec.json5");
            case YAML ->  PluginBundle.message("config.project.modules.item.file.codec.yaml");
            case PROPERTIES -> PluginBundle.message("config.project.modules.item.file.codec.properties");
        };
    }

    private String collapsePathMacros(@NotNull String expandedPath) {
        return PathMacroManager.getInstance(project).collapsePath(expandedPath);
    }

    private String expandPathMacros(@NotNull String path) {
        return PathMacroManager.getInstance(project).expandPath(path);
    }
}
