package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.InstanceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Reload translations action.
 * @author marhali
 */
public class ReloadAction extends AnAction {

    public ReloadAction() {
        super(ResourceBundle.getBundle("messages").getString("action.reload"),
                null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        InstanceManager manager = InstanceManager.get(e.getProject());
        manager.store().loadFromPersistenceLayer((success) -> {
            manager.bus().propagate().onUpdateData(manager.store().getData());
        });
    }
}