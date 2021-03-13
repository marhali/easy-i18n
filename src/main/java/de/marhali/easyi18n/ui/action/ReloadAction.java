package de.marhali.easyi18n.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.data.DataStore;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ReloadAction extends AnAction {

    public ReloadAction() {
        super("Reload From Disk", null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        try {
            DataStore.getInstance(e.getProject()).reloadFromDisk();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}