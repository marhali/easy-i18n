package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Iterator;

/**
 * Tree model fo rendering {@link ModuleView.Tree}.
 *
 * @author marhali
 */
public class I18nTreeViewModel extends DefaultTreeModel {

    public I18nTreeViewModel(@NotNull ModuleView.Tree view) {
        super(new DefaultMutableTreeNode("root"));

        for (ModuleView.Entry entry : view.entries()) {
            MutableTreeNode targetNode = (DefaultMutableTreeNode) getRoot();

            // Traverse module entry key inside the tree until we reach the leaf
            for (String key : entry.keyHierarchy()) {
                Iterator<? extends TreeNode> childIterator = targetNode.children().asIterator();
                DefaultMutableTreeNode targetChildNode = null;
                while (childIterator.hasNext()) {
                    var child = (DefaultMutableTreeNode) childIterator.next();
                    var childUserObjet = (TreeUserObject) child.getUserObject();

                    if (!childUserObjet.isLeaf() && childUserObjet.getAsNode().name().equals(key)) {
                        targetChildNode = child;
                        break;
                    }
                }

                if (targetChildNode == null) {
                    // Children for key has not been created yet. Let's do it
                    var userObject = new TreeUserObject.Node(key, !entry.missingLocaleIds().isEmpty());
                    targetChildNode = new DefaultMutableTreeNode(userObject);
                    targetNode.insert(targetChildNode, targetNode.getChildCount());
                } else {
                    // Reevaluate existing child node to check if missingValues might be checked
                    if (!((TreeUserObject) targetChildNode.getUserObject()).getAsNode().missingValues() && !entry.missingLocaleIds().isEmpty()) {
                        targetChildNode.setUserObject(new TreeUserObject.Node(key, true));
                    }
                }

                targetNode = targetChildNode;
            }

            // We always render a child for every module locale to support filling missing values
            for (LocaleId localeId : view.locales()) {
                var value = entry.content().values().get(localeId);
                var userData = new TreeUserObject.Leaf(localeId, value, entry.duplicateLocaleIds().contains(localeId));
                targetNode.insert(new DefaultMutableTreeNode(userData), targetNode.getChildCount());
            }
        }
    }
}
