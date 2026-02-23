package de.marhali.easyi18n.idea.toolwindow.ui;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.action.CollapseAllAction;
import de.marhali.easyi18n.idea.action.ExpandAllAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Tree view panel for the translations tool window content tab.
 *
 * @author marhali
 */
public class I18nTreeViewPanel implements ViewPanel<ModuleView.Tree> {

    private final @NotNull Project project;
    private final @NotNull ModuleId moduleId;

    private final @NotNull Tree tree;
    private final @NotNull JBScrollPane treeScrollPane;

    public I18nTreeViewPanel(@NotNull Project project, @NotNull ModuleId moduleId) {
        this.project = project;
        this.moduleId = moduleId;

        this.tree = new Tree();
        this.treeScrollPane = new JBScrollPane(tree);

        tree.setRootVisible(false);
        tree.setCellRenderer(new TreeNodeRenderer());
    }

    public @NotNull JComponent getComponent() {
        return this.treeScrollPane;
    }

    @Override
    public void setView(@NotNull ModuleView.Tree view) {
        tree.setModel(new I18nTreeViewModel(view));
    }

    @Override
    public @Nullable JComponent getToolbar() {
        DefaultActionGroup group = new DefaultActionGroup("Tree Actions", false);

        group.add(new ExpandAllAction(this::expandAll));
        group.add(new CollapseAllAction(this::collapseAll));

        ActionToolbar actionToolbar = ActionManager.getInstance()
            .createActionToolbar("Tree Actions", group, false);

        actionToolbar.setTargetComponent(this.tree);

        return actionToolbar.getComponent();
    }

    private void collapseAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.collapseRow(i);
        }
    }

    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
}
