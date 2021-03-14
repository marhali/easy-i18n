package de.marhali.easyi18n.ui.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;

import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.service.DataStore;

import javax.swing.*;
import java.awt.*;

/**
 * Plugin configuration dialog.
 * @author marhali
 */
public class SettingsDialog {

    private final Project project;

    private TextFieldWithBrowseButton pathText;
    private JBTextField previewText;

    public SettingsDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        String localesPath = SettingsService.getInstance(project).getState().getLocalesPath();
        String previewLocale = SettingsService.getInstance(project).getState().getPreviewLocale();

        if(prepare(localesPath, previewLocale).show() == DialogWrapper.OK_EXIT_CODE) { // Save changes
            SettingsService.getInstance(project).getState().setLocalesPath(pathText.getText());
            SettingsService.getInstance(project).getState().setPreviewLocale(previewText.getText());

            // Reload instance
            DataStore.getInstance(project).reloadFromDisk();
        }
    }

    private DialogBuilder prepare(String localesPath, String previewLocale) {
        JPanel rootPanel = new JPanel(new GridLayout(0, 1, 2, 2));

        JBLabel pathLabel = new JBLabel("Locales directory");
        pathText = new TextFieldWithBrowseButton(new JTextField(localesPath));

        pathLabel.setLabelFor(pathText);
        pathText.addBrowseFolderListener("Locales Directory", null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        rootPanel.add(pathLabel);
        rootPanel.add(pathText);

        JBLabel previewLabel = new JBLabel("Preview locale");
        previewText = new JBTextField(previewLocale);
        previewLabel.setLabelFor(previewText);

        rootPanel.add(previewLabel);
        rootPanel.add(previewText);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle("Settings");
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(rootPanel);

        return builder;
    }
}