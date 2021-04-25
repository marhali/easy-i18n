package de.marhali.easyi18n.ui.tabs;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import de.marhali.easyi18n.service.DataStore;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.model.DataSynchronizer;
import de.marhali.easyi18n.model.Translations;
import de.marhali.easyi18n.model.KeyedTranslation;
import de.marhali.easyi18n.model.TranslationDelete;
import de.marhali.easyi18n.model.tree.TreeModelTranslator;
import de.marhali.easyi18n.ui.action.treeview.CollapseTreeViewAction;
import de.marhali.easyi18n.ui.action.treeview.ExpandTreeViewAction;
import de.marhali.easyi18n.ui.dialog.EditDialog;
import de.marhali.easyi18n.ui.listener.DeleteKeyListener;
import de.marhali.easyi18n.ui.listener.PopupClickListener;
import de.marhali.easyi18n.ui.renderer.TreeRenderer;
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
public class TreeView implements DataSynchronizer {

    private final Project project;

    private JPanel rootPanel;
    private JPanel toolBarPanel;
    private JPanel containerPanel;

    private Tree tree;

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

        JComponent actionToolbar = ActionManager.getInstance()
                .createActionToolbar("TranslationsActions", group, false).getComponent();

        toolBarPanel.add(actionToolbar);
    }

    @Override
    public void synchronize(@NotNull Translations translations, @Nullable String searchQuery) {
        tree.setModel(new TreeModelTranslator(project, translations, searchQuery));

        if(searchQuery != null && !searchQuery.isEmpty()) {
            expandAll().run();
        }
    }

    private void handlePopup(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());

        if(path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

            if(node.getUserObject() instanceof PresentationData) {
                String fullPath = TreeUtil.getFullPath(path);
                LocalizedNode localizedNode = DataStore.getInstance(project).getTranslations().getNode(fullPath);

                if(localizedNode != null) {
                    new EditDialog(project,new KeyedTranslation(fullPath, localizedNode.getValue())).showAndHandle();
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

                DataStore.getInstance(project).processUpdate(
                        new TranslationDelete(new KeyedTranslation(fullPath, null)));
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