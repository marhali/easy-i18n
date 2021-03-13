package de.marhali.easyi18n.ui.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Action to collapse all tree nodes with children.
 * @author marhali
 */
public class CollapseTreeViewAction extends AnAction {

    private final Runnable collapseRunnable;

    public CollapseTreeViewAction(Runnable collapseRunnable) {
        super("Collapse Tree", null, AllIcons.Actions.Collapseall);
        this.collapseRunnable = collapseRunnable;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        collapseRunnable.run();
    }
}