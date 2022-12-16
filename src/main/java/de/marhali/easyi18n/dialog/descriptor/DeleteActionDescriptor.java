package de.marhali.easyi18n.dialog.descriptor;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Delete action which represents the delete button on the edit translation dialog.
 * Action can be monitored using the exit code for the opened dialog. See EXIT_CODE.
 * @author marhali
 */
public class DeleteActionDescriptor extends AbstractAction {

    public static final int EXIT_CODE = 10;

    private final DialogWrapper dialog;

    public DeleteActionDescriptor(@NotNull DialogWrapper dialog) {
        super(ResourceBundle.getBundle("messages").getString("action.delete"));
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dialog.close(EXIT_CODE, false);
    }
}