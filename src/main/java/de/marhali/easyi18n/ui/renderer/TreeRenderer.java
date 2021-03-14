package de.marhali.easyi18n.ui.renderer;

import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.NlsSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * Similar to {@link NodeRenderer} but will will override {@link #getPresentation(Object)} to
 * make {@link ItemPresentation} visible.
 * @author marhali
 */
public class TreeRenderer extends NodeRenderer {

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, @NlsSafe Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
    }

    @Override
    protected @Nullable ItemPresentation getPresentation(Object node) {
        if(node instanceof ItemPresentation) {
            return (ItemPresentation) node;
        } else {
            return super.getPresentation(node);
        }
    }
}