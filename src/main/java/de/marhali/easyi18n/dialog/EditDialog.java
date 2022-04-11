package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.dialog.descriptor.DeleteActionDescriptor;
import de.marhali.easyi18n.model.action.TranslationDelete;
import de.marhali.easyi18n.model.action.TranslationUpdate;
import de.marhali.easyi18n.model.Translation;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Dialog to edit or delete an existing translation.
 * @author marhali
 */
public class EditDialog extends TranslationDialog {

    /**
     * Constructs a new edit dialog with the provided translation
     * @param project Opened project
     * @param origin Translation to edit
     */
    public EditDialog(@NotNull Project project, @NotNull Translation origin) {
        super(project, origin);
    }

    @Override
    protected @NotNull DialogBuilder configure(@NotNull JComponent centerPanel) {
        DialogBuilder builder = new DialogBuilder();
        builder.setTitle(bundle.getString("action.edit"));
        builder.removeAllActions();
        builder.addCancelAction();
        builder.addActionDescriptor(new DeleteActionDescriptor());
        builder.addOkAction();
        builder.setCenterPanel(centerPanel);
        return builder;
    }

    @Override
    protected void handleExit(int exitCode) {
        switch (exitCode) {
            case DialogWrapper.OK_EXIT_CODE:
                InstanceManager.get(project).processUpdate(new TranslationUpdate(origin, getState()));
                break;
            case DeleteActionDescriptor.EXIT_CODE:
                InstanceManager.get(project).processUpdate(new TranslationDelete(origin));
                break;
        }
    }
}