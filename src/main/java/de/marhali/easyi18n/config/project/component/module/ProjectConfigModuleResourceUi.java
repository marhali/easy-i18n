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

    private JBTextField fileFolderPattern;
    private JBTextField fileContentPattern;
    private JBTextField keyPattern;
    private JBTextField rootDirectory;

    protected ProjectConfigModuleResourceUi(Project project) {
        super(project);
    }

    @Override
    public void buildComponent(FormBuilder formBuilder) {
        // Title
        formBuilder.addComponent(new TitledSeparator(i18n.getString("config.project.modules.item.resource.title")));

        // Folder structure
        fileFolderPattern = new JBTextField();
        fileFolderPattern.setToolTipText(i18n.getString("config.project.modules.item.folder.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.folder.label"), fileFolderPattern, 1, false);

        // File structure
        fileContentPattern = new JBTextField();
        fileContentPattern.setToolTipText(i18n.getString("config.project.modules.item.file.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.file.label"), fileContentPattern, 1, false);

        // Key structure
        keyPattern = new JBTextField();
        keyPattern.setToolTipText(i18n.getString("config.project.modules.item.key.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.key.label"), keyPattern, 1, false);

        // Root directory
        rootDirectory = new JBTextField();
        rootDirectory.setToolTipText(i18n.getString("config.project.modules.item.dir.tooltip"));

        formBuilder.addLabeledComponent(i18n.getString("config.project.modules.item.dir.label"), rootDirectory, 1, false);
    }

    @Override
    public boolean isModified() {
        var equals = fileFolderPattern.getText().equals(state.getFileFolderPattern())
            && fileContentPattern.getText().equals(state.getFileContentPattern())
            && keyPattern.getText().equals(state.getKeyPattern())
            && rootDirectory.getText().equals(state.getRootDirectory());

        return !equals;
    }

    @Override
    public void applyChangesToState() {
        state.setFileFolderPattern(fileFolderPattern.getText());
        state.setFileContentPattern(fileContentPattern.getText());
        state.setKeyPattern(keyPattern.getText());
        state.setRootDirectory(rootDirectory.getText());
    }

    @Override
    public void applyStateToComponent(ProjectConfigModule state) {
        super.applyStateToComponent(state);

        fileFolderPattern.setText(state.getFileFolderPattern());
        fileContentPattern.setText(state.getFileContentPattern());
        keyPattern.setText(state.getKeyPattern());
        rootDirectory.setText(state.getRootDirectory());
    }
}
