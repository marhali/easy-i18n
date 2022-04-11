package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Base for add and edit translation dialogs.
 * @author marhali
 */
abstract class TranslationDialog {

    protected static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    protected final @NotNull Project project;
    protected final @NotNull ProjectSettings settings;
    protected final @NotNull KeyPathConverter converter;
    protected final @NotNull Translation origin;

    protected final JTextField keyField;
    protected final JTextField descriptionField;
    protected final Map<String, JTextField> localeValueFields;

    /**
     * Constructs a new translation dialog.
     * @param project Opened project
     * @param origin Prefill translation
     */
    protected TranslationDialog(@NotNull Project project, @NotNull Translation origin) {
        this.project = project;
        this.settings = ProjectSettingsService.get(project).getState();
        this.converter = new KeyPathConverter(settings);
        this.origin = origin;

        // Fields
        TranslationValue value = origin.getValue();

        this.keyField = new JBTextField(converter.toString(origin.getKey()));
        this.descriptionField = new JBTextField(value != null ? value.getDescription() : null);
        this.localeValueFields = new HashMap<>();

        for(String locale : InstanceManager.get(project).store().getData().getLocales()) {
            localeValueFields.put(locale, new JBTextField(value != null ? value.get(locale) : null));
        }
    }

    /**
     * Implementation needs to configure the dialog. E.g. title, actions, ...
     * The implementation needs to set the provided centerPanel as the view panel.
     * @param centerPanel GUI to set on the dialog builder
     * @return configured dialog builder
     */
    protected abstract @NotNull DialogBuilder configure(@NotNull JComponent centerPanel);

    /**
     * Implementation needs to handle exit
     * @param exitCode See {@link com.intellij.openapi.ui.DialogWrapper} for exit codes
     */
    protected abstract void handleExit(int exitCode);

    /**
     * Opens the translation modal and applies the appropriate logic on modal close.
     * Internally, the {@link #handleExit(int)} method will be called to determine finalization logic.
     */
    public void showAndHandle() {
        int exitCode = createDialog().show();
        handleExit(exitCode);
    }

    /**
     * Retrieve current modal state.
     * @return Translation
     */
    protected @NotNull Translation getState() {
        KeyPath key = converter.fromString(keyField.getText());
        TranslationValue value = new TranslationValue();

        value.setDescription(descriptionField.getText());

        for(Map.Entry<String, JTextField> entry : localeValueFields.entrySet()) {
            value.put(entry.getKey(), entry.getValue().getText());
        }

        return new Translation(key, value);
    }

    private DialogBuilder createDialog() {
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(bundle.getString("translation.key"), keyField, true)
                .addLabeledComponent(bundle.getString("translation.description"), descriptionField, 6, true)
                .addComponent(createLocalesPanel(), 12)
                .getPanel();

        panel.setMinimumSize(new Dimension(200, 150));

        return configure(panel);
    }

    private JComponent createLocalesPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        for(Map.Entry<String, JTextField> localeEntry : localeValueFields.entrySet()) {
            builder.addLabeledComponent(localeEntry.getKey(), localeEntry.getValue(), 6, true);
        }

        JScrollPane scrollPane = new JBScrollPane(builder.getPanel());

        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), bundle.getString("translation.locales")));

        return scrollPane;
    }
}