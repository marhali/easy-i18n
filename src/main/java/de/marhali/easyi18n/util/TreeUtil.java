package de.marhali.easyi18n.util;

import com.intellij.ide.projectView.PresentationData;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Swing tree utility
 * @author marhali
 */
public class TreeUtil {

    /**
     * Constructs the full path for a given {@link TreePath}
     * @param path TreePath
     * @return Full key (e.g user.username.title)
     */
    public static String getFullPath(TreePath path) {
        StringBuilder builder = new StringBuilder();

        for (Object obj : path.getPath()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            Object value = node.getUserObject();
            String section = value instanceof PresentationData ?
                    ((PresentationData) value).getPresentableText() : String.valueOf(value);

            if(section == null) { // Skip empty sections
                continue;
            }

            if(builder.length() != 0) {
                builder.append(".");
            }

            builder.append(section);
        }

        return builder.toString();
    }
}