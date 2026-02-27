package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.Consumer;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.tree.TreeUtil;
import de.marhali.easyi18n.core.application.command.RemoveI18nRecordsCommand;
import de.marhali.easyi18n.core.application.command.RemoveI18nValueCommand;
import de.marhali.easyi18n.core.application.command.UpdateI18nValueCommand;
import de.marhali.easyi18n.core.application.command.UpdatePartialI18nKeyCommand;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.idea.action.CollapseAllAction;
import de.marhali.easyi18n.idea.action.ExpandAllAction;
import de.marhali.easyi18n.idea.dialog.TranslationDialogFactory;
import de.marhali.easyi18n.idea.toolwindow.listener.PopupClickListener;
import de.marhali.easyi18n.idea.toolwindow.ui.ViewPanel;
import de.marhali.easyi18n.listener.DeleteKeyListener;
import de.marhali.easyi18n.listener.ReturnKeyListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Tree view panel for the translations tool window content tab.
 *
 * @author marhali
 */
public class I18nTreeViewPanel implements ViewPanel<ModuleView.Tree> {

    private final @NotNull Project project;
    private final @NotNull ModuleId moduleId;
    private final @NotNull Consumer<@NotNull Command> handleCommandAsync;

    private final @NotNull Tree tree;
    private final @NotNull JBScrollPane treeScrollPane;

    public I18nTreeViewPanel(@NotNull Project project, @NotNull ModuleId moduleId, @NotNull Consumer<@NotNull Command> handleCommandAsync) {
        this.project = project;
        this.moduleId = moduleId;
        this.handleCommandAsync = handleCommandAsync;

        this.tree = new Tree() {
            // Override value to text converter to support tree node editor
            @Override
            public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value instanceof DefaultMutableTreeNode node) {
                    if (node.getUserObject() instanceof TreeUserObject userObject) {
                        if (userObject instanceof TreeUserObject.Leaf userObjectLeaf) {
                            return userObjectLeaf.value() != null ? userObjectLeaf.value().toInputString() : "";
                        } else {
                            return userObject.getAsNode().name();
                        }
                    }
                }

                return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
            }
        };
        this.treeScrollPane = new JBScrollPane(tree);

        tree.setBorder(JBUI.Borders.customLineLeft(JBColor.border()));
        tree.setRootVisible(false);
        tree.setCellRenderer(new TreeNodeRenderer());
        tree.setEditable(true);
        tree.addMouseListener(new PopupClickListener((_e) -> showSelectionAsDialog()));
        tree.addKeyListener(new ReturnKeyListener(this::showSelectionAsDialog));
        tree.addKeyListener(new DeleteKeyListener(this::handleDeleteSelections));
    }

    public @NotNull JComponent getComponent() {
        return this.treeScrollPane;
    }

    @Override
    public void setView(@NotNull ModuleView.Tree view, @Nullable I18nKey key) {
        var expandedRows = getExpandedRows();
        tree.setModel(new I18nTreeViewModel(view, this::handleTreeUpdate));
        expandRows(expandedRows);

        if (key != null) {
            focusKey(key);
        }
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

    private List<Integer> getExpandedRows() {
        var expandedRows = new ArrayList<Integer>();

        for (int i = 0; i < tree.getRowCount(); i++) {
            if (tree.isExpanded(i)) {
                expandedRows.add(i);
            }
        }

        return expandedRows;
    }

    private void expandRows(@NotNull List<Integer> expandedRows) {
        for (Integer expandedRow : expandedRows) {
            tree.expandRow(expandedRow);
        }
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

    private void showSelectionAsDialog() {
        var treePath = tree.getSelectionPath();

        if (treePath == null) {
            // Invalid path
            return;
        }

        if (treePath.getLastPathComponent() instanceof DefaultMutableTreeNode node) {
            if (node.getUserObject() instanceof TreeUserObject.Leaf userObject) {
                // Edit popup works for now only on leaf nodes (one node above should also be possible)
                TranslationDialogFactory.createEditDialog(
                    project,
                    moduleId,
                    userObject.key(),
                    DialogWrapper::show
                );
            }
        }
    }

    private void focusKey(@NotNull I18nKey key) {
        var targetNode = TreeUtil.findNode((DefaultMutableTreeNode) tree.getModel().getRoot(),
            (node) -> node.isLeaf()
                && node.getUserObject() instanceof TreeUserObject.Leaf userObjectLeaf
                && userObjectLeaf.key().equals(key));

        if (targetNode != null) {
            var path = new TreePath(((DefaultMutableTreeNode) targetNode.getParent()).getPath());
            tree.getSelectionModel().setSelectionPath(path);
            tree.scrollPathToVisible(path);
            tree.expandPath(path);
        }
    }

    private void handleDeleteSelections() {
        var selectionTreePaths = tree.getSelectionPaths();

        if (selectionTreePaths == null) {
            // Invalid selections
            return;
        }

        Set<I18nKey> keysToDelete = new HashSet<>();

        for (TreePath selectionTreePath : selectionTreePaths) {
            if (selectionTreePath.getLastPathComponent() instanceof DefaultMutableTreeNode node) {
                collectI18nKeysForTreeNode(node, keysToDelete);
            }
        }

        if (keysToDelete.isEmpty()) {
            // Dont process update if nothing
            return;
        }

        handleCommandAsync.accept(new RemoveI18nRecordsCommand(moduleId, keysToDelete));
    }

    private void collectI18nKeysForTreeNode(@NotNull TreeNode node, @NotNull Set<I18nKey> keys) {
        if (node instanceof DefaultMutableTreeNode defaultMutableTreeNode) {
            if (defaultMutableTreeNode.getUserObject() instanceof TreeUserObject.Leaf userObject) {
                keys.add(userObject.key());
            } else {
                Iterator<TreeNode> childrenIterator = defaultMutableTreeNode.children().asIterator();
                while (childrenIterator.hasNext()) {
                    collectI18nKeysForTreeNode(childrenIterator.next(), keys);
                }
            }
        }
    }

    private void handleTreeUpdate(@NotNull TreeUpdate treeUpdate) {
        switch (treeUpdate) {
            case TreeUpdate.Key keyUpdate -> {
                if (!keyUpdate.newPartName().isBlank()) {
                    handleCommandAsync.accept(new UpdatePartialI18nKeyCommand(
                        moduleId,
                        keyUpdate.parentParts(),
                        keyUpdate.previousPartName(),
                        keyUpdate.newPartName()
                    ));
                }
            }
            case TreeUpdate.Value valueUpdate -> {
                if (valueUpdate.newValue().isBlank()) { // Remove value
                    handleCommandAsync.accept(new RemoveI18nValueCommand(
                        moduleId,
                        valueUpdate.key(),
                        valueUpdate.localeId()
                    ));
                } else { // Update value
                    handleCommandAsync.accept(new UpdateI18nValueCommand(
                        moduleId,
                        valueUpdate.key(),
                        valueUpdate.localeId(),
                        I18nValue.fromInputString(valueUpdate.newValue())
                    ));
                }
            }
        }
    }
}
