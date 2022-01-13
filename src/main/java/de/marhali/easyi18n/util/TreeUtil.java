package de.marhali.easyi18n.util;

import com.intellij.ide.projectView.PresentationData;
import de.marhali.easyi18n.model.KeyPath;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Swing tree utility
 * @author marhali
 */
public class TreeUtil {

    /**
     * Constructs the full path for a given {@link TreePath}
     * @param treePath TreePath
     * @return Corresponding key path
     */
    public static KeyPath getFullPath(TreePath treePath) {
        KeyPath keyPath = new KeyPath();

        for (Object obj : treePath.getPath()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            Object value = node.getUserObject();
            String section = value instanceof PresentationData ?
                    ((PresentationData) value).getPresentableText() : String.valueOf(value);

            if(value == null) { // Skip empty sections
                continue;
            }

            keyPath.add(section);
        }

        return keyPath;
    }
}