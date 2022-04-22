package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;

import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.service.WindowManager;
import de.marhali.easyi18n.util.KeyPathConverter;
import de.marhali.easyi18n.util.TreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Add translation action.
 * @author marhai
 */
public class AddAction extends AnAction {

    public AddAction() {
        super(ResourceBundle.getBundle("messages").getString("action.add"),
                null, AllIcons.General.Add);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        new AddDialog(project, detectPreKey(project), null).showAndHandle();
    }

    /**
     * Detects a selected translation key in our tool-window.
     * @param project Opened project
     * @return Found key to prefill translation key or null if not applicable
     */
    private @Nullable KeyPath detectPreKey(@NotNull Project project) {
        KeyPathConverter converter = new KeyPathConverter(project);
        WindowManager window = WindowManager.getInstance();

        if(window.getToolWindow() == null) {
            return null;
        }

        Content manager = window.getToolWindow().getContentManager().getSelectedContent();

        if(manager == null) {
            return null;
        }

        if(manager.getDisplayName().equals(
                ResourceBundle.getBundle("messages").getString("view.tree.title"))) { // Tree View

            TreePath path = window.getTreeView().getTree().getSelectionPath();

            if(path != null) {
                return TreeUtil.getFullPath(path);
            }

        } else { // Table View
            int row = window.getTableView().getTable().getSelectedRow();

            if(row >= 0) {
                String path = String.valueOf(window.getTableView().getTable().getValueAt(row, 0));
                return converter.fromString(path);
            }
        }

        return null;
    }
}