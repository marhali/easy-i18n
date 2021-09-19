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
    private JBTextField previewText;
    private JBTextField prefixText;
    private JBCheckBox codeAssistanceCheckbox;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();
        String filePattern = SettingsService.getInstance(project).getState().getFilePattern();
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();
        String prefixLocale = SettingsService.getInstance(project).getState().getPrefix();
        boolean codeAssistance = SettingsService.getInstance(project).getState().isCodeAssistance();

        if(prepare(localesPath, filePattern, previewLocale, prefixLocale, codeAssistance).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            SettingsService.getInstance(project).getState().setLocalesPath(pathText.getText());
            SettingsService.getInstance(project).getState().setFilePattern(filePatternText.getText());
            SettingsService.getInstance(project).getState().setPreviewLocale(previewText.getText());
            SettingsService.getInstance(project).getState().setCodeAssistance(codeAssistanceCheckbox.isSelected());
            SettingsService.getInstance(project).getState().setPrefix(prefixText.getText());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(String localesPath, String filePattern, String previewLocale, String prefixLocale, boolean codeAssistance) {
        JPanel rootPanel = new JPanel(new GridLayout(0, 1, 2, 2));

        JBLabel pathLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.text"));
        pathText = new TextFieldWithBrowseButton(new JTextField(localesPath));

        pathLabel.setLabelFor(pathText);
        pathText.addBrowseFolderListener(ResourceBundle.getBundle("messages").getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        rootPanel.add(pathLabel);
        rootPanel.add(pathText);

        JBLabel filePatternLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.file-pattern"));
        filePatternText = new JBTextField(filePattern);

        rootPanel.add(filePatternLabel);
        rootPanel.add(filePatternText);


        JBLabel previewLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.preview"));
        previewText = new JBTextField(previewLocale);
        previewLabel.setLabelFor(previewText);

        rootPanel.add(previewLabel);
        rootPanel.add(previewText);

        codeAssistanceCheckbox = new JBCheckBox(ResourceBundle.getBundle("messages").getString("settings.editor.assistance"));
        codeAssistanceCheckbox.setSelected(codeAssistance);

        JBLabel prefixLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("settings.path.prefix"));
        prefixText = new JBTextField(prefixLocale);
        rootPanel.add(prefixLabel);
        rootPanel.add(prefixText);

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