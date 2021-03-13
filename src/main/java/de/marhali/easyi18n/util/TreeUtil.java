package de.marhali.easyi18n.util;

import com.intellij.ide.projectView.PresentationData;
import de.marhali.easyi18n.data.LocalizedNode;
import de.marhali.easyi18n.model.tree.TreeModelTranslator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TreeUtil {

    public static String getFullPath(TreePath path) {
        StringBuilder builder = new StringBuilder();


        for (Object obj : path.getPath()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            Object value = node.getUserObject();
            String section = value instanceof PresentationData ?
                    ((PresentationData) value).getPresentableText() : String.valueOf(value);

            if(section == null || section.equals(LocalizedNode.ROOT_KEY)) { // Skip root node
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