package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Tree model fo rendering {@link ModuleView.Tree}.
 *
 * @author marhali
 */
public class I18nTreeViewModel extends DefaultTreeModel {

    private final @NotNull Consumer<@NotNull TreeUpdate> onHandleUpdate;

    public I18nTreeViewModel(@NotNull ModuleView.Tree view, @NotNull Consumer<@NotNull TreeUpdate> onHandleUpdate) {
        super(new DefaultMutableTreeNode("root"));

        this.onHandleUpdate = onHandleUpdate;

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
                var key = entry.key();
                var value = entry.content().values().get(localeId);
                var duplicatedValue = entry.duplicateLocaleIds().contains(localeId);
                var userData = new TreeUserObject.Leaf(key, localeId, value, duplicatedValue);
                targetNode.insert(new DefaultMutableTreeNode(userData), targetNode.getChildCount());
            }
        }
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        if (path.getLastPathComponent() instanceof DefaultMutableTreeNode node) {
            if (node.getUserObject() instanceof TreeUserObject userObject) {
                // Update locale value
                if (userObject instanceof TreeUserObject.Leaf userObjectLeaf) {
                    // Get previous value as input string
                    var previousValue = userObjectLeaf.value() != null ? userObjectLeaf.value().toInputString() : "";

                    // Only process update if value has been actually changed in comparison to previous value
                    if (!Objects.equals(newValue, previousValue)) {
                        var update = new TreeUpdate.Value(userObjectLeaf.key(), userObjectLeaf.localeId(), (String) newValue);
                        this.onHandleUpdate.accept(update);
                    }
                } else {
                    // Update key (replace partial key part)
                    var previousName = userObject.getAsNode().name();

                    // Only process update if value has been actually changed in comparison to previous value
                    if (!Objects.equals(newValue, previousName)) {
                        var parentNames = getParentKeyParts(path);
                        var update = new TreeUpdate.Key(parentNames, previousName, (String) newValue);
                        this.onHandleUpdate.accept(update);
                    }
                }
            }
        }
    }

    private @NotNull List<@NotNull String> getParentKeyParts(@NotNull TreePath path) {
        var parentNames = new ArrayList<String>();

        for (Object currentParent : path.getParentPath().getPath()) {
            if (currentParent instanceof DefaultMutableTreeNode currentParentNode) {
                if (currentParentNode.getUserObject() instanceof TreeUserObject.Node currentParentUserObject) {
                    parentNames.add(currentParentUserObject.name());
                }
            }
        }

        return parentNames;
    }
}
