package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.action.TranslationCreate;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.settings.ProjectSettingsService;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Dialog to create a new translation with all associated locale values.
 * Supports optional prefill technique for translation key or locale value.
 * @author marhali
 */
public class AddDialog extends TranslationDialog {

    /**
     * Constructs a new create dialog with prefilled fields
     * @param project Opened project
     * @param prefillKey Prefill translation key
     * @param prefillLocale  Prefill preview locale value
     */
    public AddDialog(@NotNull Project project, @Nullable KeyPath prefillKey, @Nullable String prefillLocale) {
        super(project, new Translation(prefillKey != null ? prefillKey : new KeyPath(),
                        prefillLocale != null
                                ? new TranslationValue(ProjectSettingsService.get(project).getState().getPreviewLocale(), prefillLocale)
                                : null)
        );
    }

    /**
     * Constructs a new create dialog without prefilled fields.
     * @param project Opened project
     */
    public AddDialog(@NotNull Project project) {
        this(project, new KeyPath(), "");
    }


    @Override
    protected @NotNull DialogBuilder configure(@NotNull JComponent centerPanel) {
        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(bundle.getString("action.add"));
        builder.removeAllActions();
        builder.addOkAction();
        builder.addCancelAction();
        builder.setCenterPanel(centerPanel);
        return builder;
    }

    @Override
    protected void handleExit(int exitCode) {
        if(exitCode == DialogWrapper.OK_EXIT_CODE) {
            InstanceManager.get(project).processUpdate(new TranslationCreate(getState()));
        }
    }
}
