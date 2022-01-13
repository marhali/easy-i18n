package de.marhali.easyi18n.tabs;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.listener.ReturnKeyListener;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.model.bus.BusListener;
import de.marhali.easyi18n.action.treeview.CollapseTreeViewAction;
import de.marhali.easyi18n.action.treeview.ExpandTreeViewAction;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.listener.DeleteKeyListener;
import de.marhali.easyi18n.listener.PopupClickListener;
import de.marhali.easyi18n.renderer.TreeRenderer;
import de.marhali.easyi18n.service.SettingsService;
import de.marhali.easyi18n.tabs.mapper.TreeModelMapper;
import de.marhali.easyi18n.util.TreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ResourceBundle;

/**
 * Show translation state as tree.
 *
 * @author marhali
 */
public class TreeView implements BusListener {

    private final Tree tree;

    private final Project project;

    private TreeModelMapper currentMapper;

    private JPanel rootPanel;
    private JPanel toolBarPanel;
    private JPanel containerPanel;

    public TreeView(Project project) {
        this.project = project;

        tree = new Tree();
        tree.setCellRenderer(new TreeRenderer());
        tree.setRootVisible(false);
        tree.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        tree.addMouseListener(new PopupClickListener(e -> showEditPopup(tree.getPathForLocation(e.getX(), e.getY()))));
        tree.addKeyListener(new ReturnKeyListener(() -> showEditPopup(tree.getSelectionPath())));
        tree.addKeyListener(new DeleteKeyListener(this::deleteSelectedNodes));

        containerPanel.add(new JBScrollPane(tree));
        placeActions();
    }

    private void placeActions() {
        DefaultActionGroup group = new DefaultActionGroup("TranslationsGroup", false);

        ExpandTreeViewAction expand = new ExpandTreeViewAction(this::expandAll);
        CollapseTreeViewAction collapse = new CollapseTreeViewAction(this::collapseAll);

        group.add(collapse);
        group.add(expand);

        ActionToolbar actionToolbar = ActionManager.getInstance()
                .createActionToolbar("TranslationsActions", group, false);

        actionToolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(actionToolbar.getComponent());
    }

    @Override
    public void onUpdateData(@NotNull TranslationData data) {
        tree.setModel(this.currentMapper = new TreeModelMapper(data, SettingsService.getInstance(project).getState()));
    }

    @Override
    public void onFocusKey(@NotNull KeyPath key) {
        if (currentMapper != null) {
            TreePath path = currentMapper.findTreePath(key);

            this.tree.getSelectionModel().setSelectionPath(path);
            this.tree.scrollPathToVisible(path);

            if (this.tree.isCollapsed(path)) {
                this.tree.expandPath(path);
            }
        }
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        if (this.currentMapper != null) {
            this.currentMapper.onSearchQuery(query);
            this.expandAll();
            this.tree.updateUI();
        }
    }

    @Override
    public void onFilterMissingTranslations(boolean filter) {
        if (this.currentMapper != null) {
            this.currentMapper.onFilterMissingTranslations(filter);
            this.expandAll();
            this.tree.updateUI();
        }
    }

    private void showEditPopup(@Nullable TreePath path) {
        if (path == null) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (!(node.getUserObject() instanceof PresentationData)) {
            return;
        }

        KeyPath fullPath = TreeUtil.getFullPath(path);
        Translation translation = InstanceManager.get(project).store().getData().getTranslation(fullPath);

        if (translation == null) {
            return;
        }

        new EditDialog(project, new KeyedTranslation(fullPath, translation)).showAndHandle();
    }

    private void deleteSelectedNodes() {
        TreePath[] paths = tree.getSelectionPaths();

        if (paths == null) {
            return;
        }

        for (TreePath path : tree.getSelectionPaths()) {
            KeyPath fullPath = TreeUtil.getFullPath(path);

            InstanceManager.get(project).processUpdate(
                    new TranslationDelete(new KeyedTranslation(fullPath, null))
            );
        }
    }

    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    private void collapseAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.collapseRow(i);
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public Tree getTree() {
        return tree;
    }
}