package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;

import de.marhali.easyi18n.settings.ProjectSettingsConfigurable;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Plugin settings action.
 * @author marhali
 */
public class SettingsAction extends AnAction {

    public SettingsAction() {
        super(ResourceBundle.getBundle("messages").getString("action.settings"),
                null, AllIcons.General.Settings);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), ProjectSettingsConfigurable.class);
    }
}