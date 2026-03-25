package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Tree node renderer with coloration support.
 *
 * @author marhali
 */
public class TreeNodeRenderer extends NodeRenderer {

    private static final SimpleTextAttributes WARNING_ATTRIBUTES = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE);

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, @NlsSafe Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode node) {
            if (node.getUserObject() instanceof TreeUserObject userObject) {

                if (userObject instanceof TreeUserObject.Leaf leafUserObject) {
                    append(leafUserObject.localeId().tag() + ": ", SimpleTextAttributes.GRAYED_ATTRIBUTES);

                    if (leafUserObject.value() != null) {
                        append(leafUserObject.value().toInputString(), leafUserObject.duplicatedValue()
                            ? WARNING_ATTRIBUTES
                            : SimpleTextAttributes.REGULAR_ATTRIBUTES
                        );
                    } else {
                        append("/", SimpleTextAttributes.ERROR_ATTRIBUTES);
                    }

                } else {
                    append(userObject.getAsNode().name(), userObject.getAsNode().missingValues()
                        ? SimpleTextAttributes.ERROR_ATTRIBUTES
                        : SimpleTextAttributes.REGULAR_ATTRIBUTES
                    );
                }

                return;
            }
        }

        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}
