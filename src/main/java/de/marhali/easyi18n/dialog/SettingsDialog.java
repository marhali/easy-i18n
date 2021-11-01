package de.marhali.easyi18n.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.service.DataStore;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Plugin configuration dialog.
 * @author marhali
 */
public class SettingsDialog {

    private final Project project;

    private TextFieldWithBrowseButton pathText;
    private JBTextField filePatternText;
    private JBTextField previewLocaleText;
    private JBTextField pathPrefixText;
    private JBCheckBox sortKeysCheckbox;
    private JBCheckBox nestedKeysCheckbox;
    private JBCheckBox codeAssistanceCheckbox;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        SettingsState state = SettingsService.getInstance(project).getState();

        if(prepare(state).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            state.setLocalesPath(pathText.getText());
            state.setFilePattern(filePatternText.getText());
            state.setPreviewLocale(previewLocaleText.getText());
            state.setPathPrefix(pathPrefixText.getText());
            state.setSortKeys(sortKeysCheckbox.isSelected());
            state.setNestedKeys(nestedKeysCheckbox.isSelected());
            state.setCodeAssistance(codeAssistanceCheckbox.isSelected());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(SettingsState state) {
        JPanel rootPanel = new JPanel(new GridLayout(0, 1, 2, 2));

        /* path */
        JBLabel pathLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.text"));
        pathText = new TextFieldWithBrowseButton(new JTextField(state.getLocalesPath()));

        pathLabel.setLabelFor(pathText);
        pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        rootPanel.add(pathLabel);
        rootPanel.add(pathText);

        /* file pattern */
        JBLabel filePatternLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.file-pattern"));
        filePatternText = new JBTextField(state.getFilePattern());

        rootPanel.add(filePatternLabel);
        rootPanel.add(filePatternText);

        /* preview locale */
        JBLabel previewLocaleLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.preview"));
        previewLocaleText = new JBTextField(state.getPreviewLocale());
        previewLocaleLabel.setLabelFor(previewLocaleText);

        rootPanel.add(previewLocaleLabel);
        rootPanel.add(previewLocaleText);

        /* path prefix */
        JBLabel pathPrefixLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.prefix"));
        pathPrefixText = new JBTextField(state.getPathPrefix());

        rootPanel.add(pathPrefixLabel);
        rootPanel.add(pathPrefixText);

        /* sort keys */
        sortKeysCheckbox = new JBCheckBox(ResourceBundle.getBundle("messages").getString("settings.keys.sort"));
        sortKeysCheckbox.setSelected(state.isSortKeys());

        rootPanel.add(sortKeysCheckbox);

        /* nested keys */
        nestedKeysCheckbox = new JBCheckBox(ResourceBundle.getBundle("messages").getString("settings.keys.nested"));
        nestedKeysCheckbox.setSelected(state.isNestedKeys());

        rootPanel.add(nestedKeysCheckbox);

        /* code assistance */
        codeAssistanceCheckbox = new JBCheckBox(ResourceBundle.getBundle("messages").getString("settings.editor.assistance"));
        codeAssistanceCheckbox.setSelected(state.isCodeAssistance());

        rootPanel.add(codeAssistanceCheckbox);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(ResourceBundle.getBundle("messages").getString("action.settings"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(rootPanel);

        return builder;
    }
}