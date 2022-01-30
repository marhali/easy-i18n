package de.marhali.easyi18n.dialog;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.io.ArrayMapper;
import de.marhali.easyi18n.model.FolderStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.bus.ParserStrategy;
import de.marhali.easyi18n.service.SettingsService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

/**
 * Plugin configuration dialog.
 * @author marhali
 */
public class SettingsDialog {

    private final Project project;

    private TextFieldWithBrowseButton pathText;
    private ComboBox<String> folderStrategyComboBox;
    private ComboBox<String> parserStrategyComboBox;
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
            state.setFolderStrategy(FolderStrategy.fromIndex(folderStrategyComboBox.getSelectedIndex()));
            state.setParserStrategy(ParserStrategy.fromIndex(parserStrategyComboBox.getSelectedIndex()));
            state.setFilePattern(filePatternText.getText());
            state.setPreviewLocale(previewLocaleText.getText());
            state.setPathPrefix(pathPrefixText.getText());
            state.setSortKeys(sortKeysCheckbox.isSelected());
            state.setNestedKeys(nestedKeysCheckbox.isSelected());
            state.setCodeAssistance(codeAssistanceCheckbox.isSelected());

            // Reload instance
            InstanceManager manager = InstanceManager.get(project);
            manager.store().loadFromPersistenceLayer((success) ->
                    manager.bus().propagate().onUpdateData(manager.store().getData()));
        }
    }

    private DialogBuilder prepare(SettingsState state) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        JPanel rootPanel = new JPanel(new GridLayout(0, 1, 2, 2));

        /* path */
        JBLabel pathLabel = new JBLabel(bundle.getString("settings.path.text"));
        pathText = new TextFieldWithBrowseButton(new JTextField(state.getLocalesPath()));

        pathLabel.setLabelFor(pathText);
        pathText.addBrowseFolderListener(bundle.getString("settings.path.title"), null, project, new FileChooserDescriptor(
                false, true, false, false, false, false));

        rootPanel.add(pathLabel);
        rootPanel.add(pathText);

        JBLabel strategyLabel = new JBLabel(bundle.getString("settings.strategy.title"));
        rootPanel.add(strategyLabel);

        JPanel strategyPanel = new JBPanel<>(new GridBagLayout());
        rootPanel.add(strategyPanel);
        GridBagConstraints constraints = new GridBagConstraints();

        /* folder strategy */
        folderStrategyComboBox = new ComboBox<>(bundle.getString("settings.strategy.folder").split(ArrayMapper.SPLITERATOR_REGEX));
        folderStrategyComboBox.setSelectedIndex(state.getFolderStrategy().toIndex());
        folderStrategyComboBox.setToolTipText(bundle.getString("settings.strategy.folder.tooltip"));
        folderStrategyComboBox.setMinimumAndPreferredWidth(256);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        strategyPanel.add(folderStrategyComboBox, constraints);

        /* parser strategy */
        parserStrategyComboBox = new ComboBox<>(bundle.getString("settings.strategy.parser").split(ArrayMapper.SPLITERATOR_REGEX));
        parserStrategyComboBox.setSelectedIndex(state.getParserStrategy().toIndex());
        parserStrategyComboBox.setToolTipText(bundle.getString("settings.strategy.parser.tooltip"));
        parserStrategyComboBox.addItemListener(handleParserChange());
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        strategyPanel.add(parserStrategyComboBox, constraints);

        /* file pattern strategy */
        filePatternText = new JBTextField(state.getFilePattern());
        filePatternText.setToolTipText(bundle.getString("settings.strategy.file-pattern.tooltip"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 1;
        strategyPanel.add(filePatternText, constraints);

        /* preview locale */
        JBLabel previewLocaleLabel = new JBLabel(bundle.getString("settings.preview"));
        previewLocaleText = new JBTextField(state.getPreviewLocale());
        previewLocaleLabel.setLabelFor(previewLocaleText);

        rootPanel.add(previewLocaleLabel);
        rootPanel.add(previewLocaleText);

        /* path prefix */
        JBLabel pathPrefixLabel = new JBLabel(bundle.getString("settings.path.prefix"));
        pathPrefixText = new JBTextField(state.getPathPrefix());

        rootPanel.add(pathPrefixLabel);
        rootPanel.add(pathPrefixText);

        /* sort keys */
        sortKeysCheckbox = new JBCheckBox(bundle.getString("settings.keys.sort"));
        sortKeysCheckbox.setSelected(state.isSortKeys());

        rootPanel.add(sortKeysCheckbox);

        /* nested keys */
        nestedKeysCheckbox = new JBCheckBox(bundle.getString("settings.keys.nested"));
        nestedKeysCheckbox.setSelected(state.isNestedKeys());

        rootPanel.add(nestedKeysCheckbox);

        /* code assistance */
        codeAssistanceCheckbox = new JBCheckBox(bundle.getString("settings.editor.assistance"));
        codeAssistanceCheckbox.setSelected(state.isCodeAssistance());

        rootPanel.add(codeAssistanceCheckbox);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(bundle.getString("action.settings"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addOkAction();
        builder.setCenterPanel(rootPanel);

        return builder;
    }

    private ItemListener handleParserChange() {
        return e -> {
          if(e.getStateChange() == ItemEvent.SELECTED) {
              // Automatically suggest file pattern option on parser change
              ParserStrategy newStrategy = ParserStrategy.fromIndex(parserStrategyComboBox.getSelectedIndex());
              filePatternText.setText(newStrategy.getExampleFilePattern());
          }
        };
    }
}