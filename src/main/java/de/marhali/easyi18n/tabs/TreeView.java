package de.marhali.easyi18n.tabs;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.dialog.EditDialog;
import de.marhali.easyi18n.listener.ReturnKeyListener;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.action.TranslationDelete;
import de.marhali.easyi18n.action.treeview.CollapseTreeViewAction;
import de.marhali.easyi18n.action.treeview.ExpandTreeViewAction;
import de.marhali.easyi18n.listener.DeleteKeyListener;
import de.marhali.easyi18n.listener.PopupClickListener;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.model.bus.FilteredBusListener;
import de.marhali.easyi18n.renderer.TreeRenderer;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.tabs.mapper.TreeModelMapper;
import de.marhali.easyi18n.util.TreeUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Show translation state as tree.
 * @author marhali
 */
public class TreeView implements FilteredBusListener {

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

        ExpandTreeViewAction expand = new ExpandTreeViewAction(this::onExpandAll);
        CollapseTreeViewAction collapse = new CollapseTreeViewAction(this::onCollapseAll);

        group.add(collapse);
        group.add(expand);

        ActionToolbar actionToolbar = ActionManager.getInstance()
                .createActionToolbar("TranslationsActions", group, false);

        actionToolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(actionToolbar.getComponent());
    }

    @Override
    public void onUpdateData(@NotNull TranslationData data) {
        List<Integer> expanded = getExpandedRows();
        tree.setModel(this.currentMapper = new TreeModelMapper(data, ProjectSettingsService.get(project).getState()));
        expanded.forEach(tree::expandRow);
    }

    private List<Integer> getExpandedRows() {
        List<Integer> expanded = new ArrayList<>();

        for(int i = 0; i < tree.getRowCount(); i++) {
            if(tree.isExpanded(i)) {
                expanded.add(i);
            }
        }

        return expanded;
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

    private void showEditPopup(@Nullable TreePath path) {
        if (path == null) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

        if (!(node.getUserObject() instanceof PresentationData)) {
            return;
        }

        KeyPath fullPath = TreeUtil.getFullPath(path);
        TranslationValue value = InstanceManager.get(project).store().getData().getTranslation(fullPath);

        if (value == null) {
            return;
        }

        new EditDialog(project, new Translation(fullPath, value)).showAndHandle();
    }

    private void deleteSelectedNodes() {
        TreePath[] selection = tree.getSelectionPaths();
        Set<KeyPath> batchDelete = new HashSet<>();

        if(selection == null) {
            return;
        }

        for (TreePath path : selection) {
            batchDelete.add(TreeUtil.getFullPath(path));
        }

        for (KeyPath key : batchDelete) {
            InstanceManager.get(project).processUpdate(new TranslationDelete(new Translation(key, null)));
        }
    }

    @Override
    public void onExpandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }

    public void onCollapseAll() {
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