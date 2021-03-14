package de.marhali.easyi18n.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.service.WindowManager;
import de.marhali.easyi18n.ui.dialog.AddDialog;
import de.marhali.easyi18n.util.TreeUtil;

import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
 * Add translation action.
 * @author marhai
 */
public class AddAction extends AnAction {

    public AddAction() {
        super("Add Translation", null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        new AddDialog(e.getProject(), detectPreKey()).showAndHandle();
    }

    private String detectPreKey() {
        WindowManager manager = WindowManager.getInstance();

        if(manager == null) {
            return null;
        }

        if(manager.getToolWindow().getContentManager().getSelectedContent().getDisplayName().equals("TreeView")) {

            TreePath path = manager.getTreeView().getTree().getSelectionPath();

            if(path != null) {
                return TreeUtil.getFullPath(path) + ".";
            }

        } else { // Table View

            int row = manager.getTableView().getTable().getSelectedRow();

            if(row >= 0) {
                String fullPath = String.valueOf(manager.getTableView().getTable().getValueAt(row, 0));
                return fullPath + ".";
            }
        }

        return null;
    }
}