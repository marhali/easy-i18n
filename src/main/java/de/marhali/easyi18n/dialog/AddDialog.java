package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationCreate;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Create translation dialog.
 * @author marhali
 */
public class AddDialog {

    private final Project project;
    private String preKey;

    private JBTextField keyTextField;
    private Map<String, JBTextField> valueTextFields;

    public AddDialog(Project project, String preKey) {
        this(project);
        this.preKey = preKey;
    }

    public AddDialog(Project project) {
        this.project = project;
    }

    public void showAndHandle() {
        int code = prepare().show();

        if(code == DialogWrapper.OK_EXIT_CODE) {
            saveTranslation();
        }
    }

    private void saveTranslation() {
        Translation translation = new Translation();

        valueTextFields.forEach((k, v) -> {
            if(!v.getText().isEmpty()) {
                translation.put(k, v.getText());
            }
        });

        TranslationCreate creation = new TranslationCreate(new KeyedTranslation(keyTextField.getText(), translation));
        InstanceManager.get(project).processUpdate(creation);
    }

    private DialogBuilder prepare() {
        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        JPanel keyPanel = new JPanel(new GridLayout(0, 1, 2, 2));
        JBLabel keyLabel = new JBLabel(ResourceBundle.getBundle("messages").getString("translation.key"));
        keyTextField = new JBTextField(this.preKey);
        keyLabel.setLabelFor(keyTextField);
        keyPanel.add(keyLabel);
        keyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        keyPanel.add(keyTextField);
        rootPanel.add(keyPanel);

        JPanel valuePanel = new JPanel(new GridLayout(0, 1, 2, 2));
        valueTextFields = new HashMap<>();

        for(String locale : InstanceManager.get(project).store().getData().getLocales()) {
            JBLabel localeLabel = new JBLabel(locale);
            JBTextField localeText = new JBTextField();
            localeLabel.setLabelFor(localeText);

            valuePanel.add(localeLabel);
            valuePanel.add(localeText);
            valueTextFields.put(locale, localeText);
        }

        JBScrollPane valuePane = new JBScrollPane(valuePanel);
        valuePane.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),
                ResourceBundle.getBundle("messages").getString("translation.locales")));
        rootPanel.add(valuePane);

        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(ResourceBundle.getBundle("messages").getString("action.add"));
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(rootPanel);

        return builder;
    }
}