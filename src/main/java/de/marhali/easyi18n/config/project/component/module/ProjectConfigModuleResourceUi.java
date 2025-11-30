package de.marhali.easyi18n.config.project.component.module;

import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.SimpleListCellRenderer;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_io.file.FileCodec;

import javax.swing.*;
import java.awt.*;

/**
 * @author marhali
 */
public class ProjectConfigModuleResourceUi extends BaseProjectConfigModuleUi {

    private JBTextField pathTemplate;
    private ComboBox<FileCodec> fileCodec;
    private JBTextField fileTemplate;
    private JBTextField keyTemplate;
    private JBTextField rootDirectory;

    protected ProjectConfigModuleResourceUi(Project project) {
        super(project);
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.modules.item.resource.title")));

        // Path template syntax
        pathTemplate = new JBTextField();
        pathTemplate.setToolTipText(i18n.getString("config.project.modules.item.path.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.path.label"), pathTemplate, 1, false);

        // File codec & template panel
        JPanel filePanel = new NonOpaquePanel(new BorderLayout());

        fileCodec = new ComboBox<>(FileCodec.values());
        fileCodec.setToolTipText(i18n.getString("config.project.modules.item.file.codec.tooltip"));
        fileCodec.setRenderer(SimpleListCellRenderer.create((label, value, index) -> label.setText(value.getDisplayName())));
        filePanel.add(fileCodec, BorderLayout.WEST);

        fileTemplate = new JBTextField();
        fileTemplate.setToolTipText(i18n.getString("config.project.modules.item.file.template.tooltip"));
        filePanel.add(fileTemplate, BorderLayout.CENTER);

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.file.label"), filePanel, 1, false);

        // Key template syntax
        keyTemplate = new JBTextField();
        keyTemplate.setToolTipText(i18n.getString("config.project.modules.item.key.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.key.label"), keyTemplate, 1, false);

        // Root directory
        rootDirectory = new JBTextField();
        rootDirectory.setToolTipText(i18n.getString("config.project.modules.item.dir.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.dir.label"), rootDirectory, 1, false);
    }

    @Override
    public boolean isModified() {
        var equals = pathTemplate.getText().equals(state.getPathTemplate())
            && fileCodec.getItem().equals(state.getFileCodec())
            && fileTemplate.getText().equals(state.getFileTemplate())
            && keyTemplate.getText().equals(state.getKeyTemplate())
            && rootDirectory.getText().equals(state.getRootDirectory());

        return !equals;
    }

    @Override
    public void applyChangesToState() {
        state.setPathTemplate(pathTemplate.getText());
        state.setFileCodec(fileCodec.getItem());
        state.setFileTemplate(fileTemplate.getText());
        state.setKeyTemplate(keyTemplate.getText());
        state.setRootDirectory(rootDirectory.getText());
    }

    @Override
    public void applyStateToComponent(ProjectConfigModule state) {
        super.applyStateToComponent(state);

        pathTemplate.setText(collapsePathMacros(state.getPathTemplate()));
        fileCodec.setItem(state.getFileCodec());
        fileTemplate.setText(state.getFileTemplate());
        keyTemplate.setText(state.getKeyTemplate());
        rootDirectory.setText(collapsePathMacros(state.getRootDirectory()));
    }

    private String collapsePathMacros(String expandedPath) {
        return PathMacroManager.getInstance(project).collapsePath(expandedPath);
    }
}
