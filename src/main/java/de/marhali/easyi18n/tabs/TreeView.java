package de.marhali.easyi18n.tabs;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationDelete;
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
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

/**
 * Show translation state as tree.
 * @author marhali
 */
public class TreeView implements BusListener {

    private final Project project;

    private JPanel rootPanel;
    private JPanel toolBarPanel;
    private JPanel containerPanel;

    private Tree tree;

    private TreeModelMapper mapper;

    public TreeView(Project project) {
        this.project = project;

        tree = new Tree();
        tree.setCellRenderer(new TreeRenderer());
        tree.setRootVisible(false);
        tree.getEmptyText().setText(ResourceBundle.getBundle("messages").getString("view.empty"));
        tree.addMouseListener(new PopupClickListener(this::handlePopup));
        tree.addKeyListener(new DeleteKeyListener(handleDeleteKey()));

        containerPanel.add(new JBScrollPane(tree));
        placeActions();
    }

    private void placeActions() {
        DefaultActionGroup group = new DefaultActionGroup("TranslationsGroup", false);

        ExpandTreeViewAction expand = new ExpandTreeViewAction(expandAll());
        CollapseTreeViewAction collapse = new CollapseTreeViewAction(collapseAll());

        group.add(collapse);
        group.add(expand);

        ActionToolbar actionToolbar = ActionManager.getInstance()
                .createActionToolbar("TranslationsActions", group, false);

        actionToolbar.setTargetComponent(toolBarPanel);
        toolBarPanel.add(actionToolbar.getComponent());
    }

    @Override
    public void onUpdateData(@NotNull TranslationData data) {
        tree.setModel(this.mapper = new TreeModelMapper(data, SettingsService.getInstance(project).getState(), null));
    }

    @Override
    public void onFocusKey(@Nullable String key) {
        if(key != null && mapper != null) {
            this.tree.scrollPathToVisible(mapper.findTreePath(key));
        }
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        // TODO: handle search functionality
    }

    private void handlePopup(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());

        if(path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            if(node.getUserObject() instanceof PresentationData) {
                String fullPath = TreeUtil.getFullPath(path);
                Translation translation = InstanceManager.get(project).store().getData().getTranslation(fullPath);

                if(translation != null) {
                    new EditDialog(project, new KeyedTranslation(fullPath, translation)).showAndHandle();
                }
            }
        }
    }

    private Runnable handleDeleteKey() {
        return () -> {
            TreePath[] paths = tree.getSelectionPaths();

            if (paths == null) {
                return;
            }

            for (TreePath path : tree.getSelectionPaths()) {
                String fullPath = TreeUtil.getFullPath(path);

                InstanceManager.get(project).processUpdate(
                        new TranslationDelete(new KeyedTranslation(fullPath, null))
                );
            }
        };
    }

    private Runnable expandAll() {
        return () -> {
            for(int i = 0; i < tree.getRowCount(); i++) {
                tree.expandRow(i);
            }
        };
    }

    private Runnable collapseAll() {
        return () -> {
            for(int i = 0; i < tree.getRowCount(); i++) {
                tree.collapseRow(i);
            }
        };
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public Tree getTree() {
        return tree;
    }
}