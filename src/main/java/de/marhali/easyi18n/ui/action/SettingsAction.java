package de.marhali.easyi18n.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import de.marhali.easyi18n.ui.dialog.SettingsDialog;
import org.jetbrains.annotations.NotNull;

/**
 * Plugin settings action.
 * @author marhali
 */
public class SettingsAction extends AnAction {

    public SettingsAction() {
        super("Settings", null, AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new SettingsDialog(e.getProject()).showAndHandle();
    }
}