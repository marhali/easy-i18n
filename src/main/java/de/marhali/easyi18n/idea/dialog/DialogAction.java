package de.marhali.easyi18n.idea.dialog;

import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * {@link TranslationDialog} actions.
 *
 * @author marhali
 */
public class DialogAction {
    /**
     * Action to delete a translation within a dialog.
     */
    public static class Delete extends AbstractAction {
        public static final int EXIT_CODE = 3;

        private final @NotNull Runnable onActionPerformed;

        public Delete(@NotNull Runnable onActionPerformed) {
            super(PluginBundle.message("dialog.translation.action.delete"));

            this.onActionPerformed = onActionPerformed;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onActionPerformed.run();
        }
    }
}
