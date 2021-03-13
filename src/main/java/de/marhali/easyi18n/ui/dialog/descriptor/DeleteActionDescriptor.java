package de.marhali.easyi18n.ui.dialog.descriptor;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Delete action which represents the delete button on the edit translation dialog.
 * Action can be monitored using the exit code for the opened dialog. See EXIT_CODE.
 * @author marhali
 */
public class DeleteActionDescriptor extends AbstractAction implements DialogBuilder.ActionDescriptor {

    public static final int EXIT_CODE = 10;

    private DialogWrapper dialogWrapper;

    public DeleteActionDescriptor() {
        super("Delete");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(dialogWrapper != null) {
            dialogWrapper.close(EXIT_CODE, false);
        }
    }

    @Override
    public Action getAction(DialogWrapper dialogWrapper) {
        this.dialogWrapper = dialogWrapper;
        return this;
    }
}