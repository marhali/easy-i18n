package de.marhali.easyi18n.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.settings.presets.NamingConvention;
import de.marhali.easyi18n.settings.presets.Preset;

import javax.swing.*;

/**
 * Mandatory for state management for the project settings component.
 *
 * @author marhali
 */
public class ProjectSettingsComponentState {

    protected ComboBox<Preset> preset;

    // Resource Configuration
    protected TextFieldWithBrowseButton localesDirectory;
    protected ComboBox<String> folderStrategy;
    protected ComboBox<String> parserStrategy;
    protected JTextField filePattern;

    protected JCheckBox includeSubDirs;
    protected JCheckBox sorting;
    protected JCheckBox addBlankLine;

    // Editor configuration
    protected JTextField namespaceDelimiter;
    protected JTextField sectionDelimiter;
    protected JTextField contextDelimiter;
    protected JTextField pluralDelimiter;
    protected JTextField defaultNamespace;
    protected JTextField previewLocale;

    protected JCheckBox nestedKeys;
    protected JCheckBox assistance;

    // Experimental configuration
    protected JCheckBox alwaysFold;

    protected JTextField flavorTemplate;
    protected ComboBox<String> KeyCaseFormater;

    protected ProjectSettingsState getState() {
        // Every field needs to provide its state
        ProjectSettingsState state = new ProjectSettingsState();

        state.setLocalesDirectory(localesDirectory.getText());
        state.setFolderStrategy(FolderStrategyType.fromIndex(folderStrategy.getSelectedIndex()));
        state.setParserStrategy(ParserStrategyType.fromIndex(parserStrategy.getSelectedIndex()));
        state.setFilePattern(filePattern.getText());

        state.setIncludeSubDirs(includeSubDirs.isSelected());
        state.setSorting(sorting.isSelected());

        state.setNamespaceDelimiter(namespaceDelimiter.getText());
        state.setSectionDelimiter(sectionDelimiter.getText());
        state.setContextDelimiter(contextDelimiter.getText());
        state.setPluralDelimiter(pluralDelimiter.getText());
        state.setDefaultNamespace(defaultNamespace.getText());
        state.setPreviewLocale(previewLocale.getText());

        state.setNestedKeys(nestedKeys.isSelected());
        state.setAssistance(assistance.isSelected());

        state.setAlwaysFold(alwaysFold.isSelected());
        state.setAddBlankLine(addBlankLine.isSelected());

        state.setFlavorTemplate(flavorTemplate.getText());

        state.setCaseFormat(NamingConvention.fromString(KeyCaseFormater.getSelectedItem().toString()));

        return state;
    }

    protected void setState(ProjectSettings state) {
        // Update every field with the new state
        localesDirectory.setText(state.getLocalesDirectory());
        folderStrategy.setSelectedIndex(state.getFolderStrategy().toIndex());
        parserStrategy.setSelectedIndex((state.getParserStrategy().toIndex()));
        filePattern.setText(state.getFilePattern());

        includeSubDirs.setSelected(state.isIncludeSubDirs());
        sorting.setSelected(state.isSorting());

        namespaceDelimiter.setText(state.getNamespaceDelimiter());
        sectionDelimiter.setText(state.getSectionDelimiter());
        contextDelimiter.setText(state.getContextDelimiter());
        pluralDelimiter.setText(state.getPluralDelimiter());
        defaultNamespace.setText(state.getDefaultNamespace());
        previewLocale.setText(state.getPreviewLocale());

        nestedKeys.setSelected(state.isNestedKeys());
        assistance.setSelected(state.isAssistance());

        alwaysFold.setSelected(state.isAlwaysFold());
        addBlankLine.setSelected(state.isAddBlankLine());
        flavorTemplate.setText(state.getFlavorTemplate());
        KeyCaseFormater.setSelectedItem(state.getCaseFormat().getName());
    }

}
