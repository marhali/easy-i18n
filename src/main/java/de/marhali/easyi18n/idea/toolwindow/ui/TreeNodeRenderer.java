package de.marhali.easyi18n.idea.toolwindow.ui;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

/**
 * Tree node renderer with coloration support.
 *
 * @author marhali
 */
public class TreeNodeRenderer extends NodeRenderer {

    @Override
    protected @Nullable ItemPresentation getPresentation(Object node) {
        if (node instanceof ItemPresentation) {
            return (ItemPresentation) node;
        } else {
            return super.getPresentation(node);
        }
    }
}
