package de.marhali.easyi18n.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.util.ui.FormBuilder;

import de.marhali.easyi18n.io.parser.ArrayMapper;
import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.settings.presets.NamingConvention;
import de.marhali.easyi18n.settings.presets.Preset;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

/**
 * Configuration panel with all possible options for this plugin.
 *
 * @author marhali
 */
public class ProjectSettingsComponent extends ProjectSettingsComponentState {

    private final Project project;
    private final ResourceBundle bundle;
    private final JPanel mainPanel;

    // Data fields are provided by the underlying state class

    public ProjectSettingsComponent(Project project) {
        this.project = project;
        this.bundle = ResourceBundle.getBundle("messages");

        this.mainPanel = FormBuilder.createFormBuilder()
                .addComponent(new JBLabel(bundle.getString("settings.hint.text")))
                .addComponent(new ActionLink(bundle.getString("settings.hint.action"),
                        (ActionListener) (var) -> BrowserUtil.browse("https://github.com/marhali/easy-i18n")))
                .addVerticalGap(24)
                .addLabeledComponent(bundle.getString("settings.preset.title"), constructPresetField(), 1, false)
                .addVerticalGap(12)
                .addComponent(new TitledSeparator(bundle.getString("settings.resource.title")))
                .addLabeledComponent(bundle.getString("settings.resource.path.title"), constructLocalesDirectoryField(), 1, false)
                .addLabeledComponent(bundle.getString("settings.resource.strategy"), constructFileStrategyPanel(), 1, false)
                .addVerticalGap(12)
                .addComponent(constructIncludeSubDirsField())
                .addComponent(constructSortingField())
                .addVerticalGap(24)
                .addComponent(new TitledSeparator(bundle.getString("settings.editor.title")))
                .addLabeledComponent(bundle.getString("settings.editor.key.title"), constructKeyStrategyPanel(), 1, false)
                .addLabeledComponent(bundle.getString("settings.editor.default-namespace.title"), constructDefaultNamespaceField(), 1, false)
                .addLabeledComponent(bundle.getString("settings.editor.preview.title"), constructPreviewLocaleField(), 1, false)
                .addVerticalGap(12)
                .addComponent(constructNestedKeysField())
                .addComponent(constructAssistanceField())
                .addVerticalGap(24)
                .addComponent(new TitledSeparator(bundle.getString("settings.experimental.title")))
                .addComponent(constructAlwaysFoldField())
                .addComponent(constructIsAddBlankLineField())
                .addVerticalGap(12)
                .addLabeledComponent(bundle.getString("settings.experimental.flavor-template"), constructFlavorTemplate(), 1, false)
                .addLabeledComponent(bundle.getString("settings.experimental.key-naming-format.title"), constructKeyCaseFormater(), 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    private JComponent constructPresetField() {
        preset = new ComboBox<>(Preset.values());
        preset.setToolTipText(bundle.getString("settings.preset.tooltip"));
        preset.setMinimumAndPreferredWidth(196);
        preset.addActionListener(e -> setState(preset.getItem().config())); // Listen to selection change
        return preset;
    }

    private JComponent constructLocalesDirectoryField() {
        localesDirectory = new TextFieldWithBrowseButton();
        localesDirectory.setToolTipText(bundle.getString("settings.resource.path.tooltip"));
        localesDirectory.addBrowseFolderListener(bundle.getString("settings.resource.path.window"),
                bundle.getString("settings.resource.path.tooltip"), project,
                new FileChooserDescriptor(false, true,
                        false, false, false, false));

        // Listen to value change
        localesDirectory.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateLocalesDirectory();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateLocalesDirectory();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateLocalesDirectory();
            }
        });

        validateLocalesDirectory();
        return localesDirectory;
    }

    private void validateLocalesDirectory() {
        // Paint red border to indicate missing value
        localesDirectory.setBorder(localesDirectory.getText().isEmpty()
                ? BorderFactory.createLineBorder(JBColor.red) : null);
    }

    private JPanel constructFileStrategyPanel() {
        JPanel panel = new JBPanel<>(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        /* folder strategy */
        folderStrategy = new ComboBox<>(bundle.getString("settings.resource.folder.items").split(ArrayMapper.SPLITERATOR_REGEX));
        folderStrategy.setToolTipText(bundle.getString("settings.resource.folder.tooltip"));
        folderStrategy.setMinimumAndPreferredWidth(256);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(folderStrategy, constraints);

        /* parser strategy */
        parserStrategy = new ComboBox<>(bundle.getString("settings.resource.parser.items").split(ArrayMapper.SPLITERATOR_REGEX));
        parserStrategy.setToolTipText(bundle.getString("settings.resource.parser.tooltip"));
        parserStrategy.addItemListener(handleParserChange());
        parserStrategy.setMinimumAndPreferredWidth(128);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(parserStrategy, constraints);

        /* file pattern strategy */
        filePattern = new JBTextField();
        filePattern.setToolTipText(bundle.getString("settings.resource.file-pattern.tooltip"));
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.weightx = 1;
        panel.add(filePattern, constraints);

        return panel;
    }

    private JComponent constructIncludeSubDirsField() {
        includeSubDirs = new JBCheckBox(bundle.getString("settings.resource.nesting.title"));
        includeSubDirs.setToolTipText(bundle.getString("settings.resource.nesting.tooltip"));
        return includeSubDirs;
    }

    private JComponent constructSortingField() {
        sorting = new JBCheckBox(bundle.getString("settings.resource.sorting.title"));
        sorting.setToolTipText(bundle.getString("settings.resource.sorting.tooltip"));
        return sorting;
    }

    private JPanel constructKeyStrategyPanel() {
        JPanel panel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));

        panel.add(new JBLabel(bundle.getString("settings.editor.key.namespace.title")));
        panel.add(namespaceDelimiter = createDelimiterField(bundle.getString("settings.editor.key.namespace.tooltip")));
        panel.add(new JBLabel(bundle.getString("settings.editor.key.section.title")));
        panel.add(sectionDelimiter = createDelimiterField(bundle.getString("settings.editor.key.section.tooltip")));
        panel.add(createBoldLabel(bundle.getString("settings.editor.key.leaf.title")));
        panel.add(contextDelimiter = createDelimiterField(bundle.getString("settings.editor.key.context.tooltip")));
        panel.add(createBoldLabel(bundle.getString("settings.editor.key.context.title")));
        panel.add(pluralDelimiter = createDelimiterField(bundle.getString("settings.editor.key.plural.tooltip")));
        panel.add(createBoldLabel(bundle.getString("settings.editor.key.plural.title")));

        return panel;
    }

    private JLabel createBoldLabel(String title) {
        JBLabel label = new JBLabel(title);
        Font font = label.getFont();
        label.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
        return label;
    }

    private JTextField createDelimiterField(String tooltip) {
        JBTextField field = new JBTextField();
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setToolTipText(tooltip);
        return field;
    }

    private JComponent constructDefaultNamespaceField() {
        defaultNamespace = new ExtendableTextField(20);
        defaultNamespace.setToolTipText(bundle.getString("settings.editor.default-namespace.tooltip"));
        return defaultNamespace;
    }

    private JComponent constructPreviewLocaleField() {
        previewLocale = new ExtendableTextField(12);
        previewLocale.setToolTipText(bundle.getString("settings.editor.preview.tooltip"));
        return previewLocale;
    }

    private JComponent constructNestedKeysField() {
        nestedKeys = new JBCheckBox(bundle.getString("settings.editor.key.nesting.title"));
        nestedKeys.setToolTipText(bundle.getString("settings.editor.key.nesting.tooltip"));
        return nestedKeys;
    }

    private JComponent constructAssistanceField() {
        assistance = new JBCheckBox(bundle.getString("settings.editor.assistance.title"));
        assistance.setToolTipText(bundle.getString("settings.editor.assistance.tooltip"));
        return assistance;
    }

    private JComponent constructAlwaysFoldField() {
        alwaysFold = new JBCheckBox(bundle.getString("settings.experimental.always-fold.title"));
        alwaysFold.setToolTipText(bundle.getString("settings.experimental.always-fold.tooltip"));
        return alwaysFold;
    }

    private JComponent constructFlavorTemplate() {
        flavorTemplate = new ExtendableTextField(20);
        flavorTemplate.setToolTipText(bundle.getString("settings.experimental.flavor-template-tooltip"));
        return flavorTemplate;
    }

    private JComponent constructKeyCaseFormater() {
        KeyCaseFormater = new ComboBox<>(NamingConvention.getEnumNames());
        KeyCaseFormater.setToolTipText(bundle.getString("settings.experimental.key-naming-format.tooltip"));
        KeyCaseFormater.setMinimumAndPreferredWidth(200);
        return KeyCaseFormater;
    }

    private JComponent constructIsAddBlankLineField() {
        addBlankLine = new JBCheckBox(bundle.getString("settings.experimental.add-blank-line.title"));
        addBlankLine.setToolTipText(bundle.getString("settings.experimental.add-blank-line.tooltip"));
        return addBlankLine;
    }


    private ItemListener handleParserChange() {
        return e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                // Automatically suggest file pattern option on parser change
                ParserStrategyType newStrategy = ParserStrategyType.fromIndex(parserStrategy.getSelectedIndex());
                filePattern.setText(newStrategy.getExampleFilePattern());
            }
        };
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
