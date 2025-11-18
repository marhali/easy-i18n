package de.marhali.easyi18n.config.project.component.module;

import com.intellij.openapi.project.Project;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

/**
 * @author marhali
 */
public class ProjectConfigModuleResourceUi extends BaseProjectConfigModuleUi {

    private JBTextField pathTemplate;
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

        // File template syntax
        fileTemplate = new JBTextField();
        fileTemplate.setToolTipText(i18n.getString("config.project.modules.item.file.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.file.label"), fileTemplate, 1, false);

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
            && fileTemplate.getText().equals(state.getFileTemplate())
            && keyTemplate.getText().equals(state.getKeyTemplate())
            && rootDirectory.getText().equals(state.getRootDirectory());

        return !equals;
    }

    @Override
    public void applyChangesToState() {
        state.setPathTemplate(pathTemplate.getText());
        state.setFileTemplate(fileTemplate.getText());
        state.setKeyTemplate(keyTemplate.getText());
        state.setRootDirectory(rootDirectory.getText());
    }

    @Override
    public void applyStateToComponent(ProjectConfigModule state) {
        super.applyStateToComponent(state);

        pathTemplate.setText(state.getPathTemplate());
        fileTemplate.setText(state.getFileTemplate());
        keyTemplate.setText(state.getKeyTemplate());
        rootDirectory.setText(state.getRootDirectory());
    }
}
