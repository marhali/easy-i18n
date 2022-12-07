package de.marhali.easyi18n.dialog;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import de.marhali.easyi18n.dialog.descriptor.DeleteActionDescriptor;
import de.marhali.easyi18n.model.action.TranslationDelete;
import de.marhali.easyi18n.model.action.TranslationUpdate;
import de.marhali.easyi18n.model.Translation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        setTitle(bundle.getString("action.edit"));
    }

    @Override
    protected Action @NotNull [] createLeftSideActions() {
        return new Action[]{ new DeleteActionDescriptor(this) };
    }

    @Override
    protected @Nullable TranslationUpdate handleExit(int exitCode) {
        switch (exitCode) {
            case DialogWrapper.OK_EXIT_CODE:
                return new TranslationUpdate(origin, getState());
            case DeleteActionDescriptor.EXIT_CODE:
                return new TranslationDelete(origin);
            default:
                return null;
        }
    }
}