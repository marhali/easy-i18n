package de.marhali.easyi18n.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

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
    private JBCheckBox codeAssistanceCheckbox;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();
        String filePattern = SettingsService.getInstance(project).getState().getFilePattern();
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        String pathPrefix = SettingsService.getInstance(project).getState().getPathPrefix();
        boolean codeAssistance = SettingsService.getInstance(project).getState().isCodeAssistance();

        if(prepare(localesPath, filePattern, previewLocale, pathPrefix, codeAssistance).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            SettingsService.getInstance(project).getState().setLocalesPath(pathText.getText());
            SettingsService.getInstance(project).getState().setFilePattern(filePatternText.getText());
            SettingsService.getInstance(project).getState().setPreviewLocale(previewLocaleText.getText());
            SettingsService.getInstance(project).getState().setCodeAssistance(codeAssistanceCheckbox.isSelected());
            SettingsService.getInstance(project).getState().setPathPrefix(pathPrefixText.getText());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(String localesPath, String filePattern, String previewLocale, String pathPrefix, boolean codeAssistance) {
        JPanel rootPanel = new JPanel(new GridLayout(0, 1, 2, 2));

        /* path */
        JBLabel pathLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.text"));
        pathText = new TextFieldWithBrowseButton(new JTextField(localesPath));

        pathLabel.setLabelFor(pathText);
        pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        rootPanel.add(pathLabel);
        rootPanel.add(pathText);

        /* file pattern */
        JBLabel filePatternLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.file-pattern"));
        filePatternText = new JBTextField(filePattern);

        rootPanel.add(filePatternLabel);
        rootPanel.add(filePatternText);

        /* preview locale */
        JBLabel previewLocaleLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.preview"));
        previewLocaleText = new JBTextField(previewLocale);
        previewLocaleLabel.setLabelFor(previewLocaleText);

        rootPanel.add(previewLocaleLabel);
        rootPanel.add(previewLocaleText);

        /* path prefix */
        JBLabel pathPrefixLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.prefix"));
        pathPrefixText = new JBTextField(pathPrefix);

        rootPanel.add(pathPrefixLabel);
        rootPanel.add(pathPrefixText);

        /* code assistance */
        codeAssistanceCheckbox = new JBCheckBox(ResourceBundle.getBundle("messages").getString("settings.editor.assistance"));
        codeAssistanceCheckbox.setSelected(codeAssistance);

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