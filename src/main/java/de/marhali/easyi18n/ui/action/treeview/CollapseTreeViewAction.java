package de.marhali.easyi18n.ui.action.treeview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Action to collapse all tree nodes with children.
 * @author marhali
 */
public class CollapseTreeViewAction extends AnAction {

    private final Runnable collapseRunnable;

    public CollapseTreeViewAction(Runnable collapseRunnable) {
        super(ResourceBundle.getBundle("messages").getString("view.tree.collapse"),
                null, AllIcons.Actions.Collapseall);

        this.collapseRunnable = collapseRunnable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        collapseRunnable.run();
    }
}