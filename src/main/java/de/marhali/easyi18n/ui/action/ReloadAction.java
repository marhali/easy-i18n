package de.marhali.easyi18n.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.service.DataStore;

import org.jetbrains.annotations.NotNull;

/**
 * Reload translations action.
 * @author marhali
 */
public class ReloadAction extends AnAction {

    public ReloadAction() {
        super("Reload From Disk", null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataStore.getInstance(e.getProject()).reloadFromDisk();
    }
}