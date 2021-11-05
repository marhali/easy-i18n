package de.marhali.easyi18n.tabs.mapper;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ui.JBColor;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.util.PathUtil;
import de.marhali.easyi18n.util.UiUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.*;
import java.util.List;
import java.util.Map;

/**
 * Mapping {@link TranslationData} to {@link TreeModel}.
 * @author marhali
 */
public class TreeModelMapper extends DefaultTreeModel {

    private final TranslationData data;
    private final SettingsState state;

    public TreeModelMapper(TranslationData data, SettingsState state, String searchQuery) {
        super(null);

        this.data = data;
        this.state = state;

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        this.generateNodes(rootNode, this.data.getRootNode());
        super.setRoot(rootNode);
    }

    private void generateNodes(DefaultMutableTreeNode parent, TranslationNode translationNode) {
        for(Map.Entry<String, TranslationNode> entry : translationNode.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childTranslationNode = entry.getValue();

            if(!childTranslationNode.isLeaf()) {
                // Nested node - run recursively
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
                this.generateNodes(childNode, childTranslationNode);
                parent.add(childNode);
            } else {
                String previewLocale = this.state.getPreviewLocale();
                String sub = "(" + previewLocale + ": " + childTranslationNode.getValue().get(previewLocale) + ")";
                String tooltip = UiUtil.generateHtmlTooltip(childTranslationNode.getValue());

                PresentationData data = new PresentationData(key, sub, null, null);
                data.setTooltip(tooltip);

                if(childTranslationNode.getValue().size() != this.data.getLocales().size()) {
                    data.setForcedTextForeground(JBColor.RED);
                }

                parent.add(new DefaultMutableTreeNode(data));
            }
        }
    }


    public @NotNull TreePath findTreePath(@NotNull String fullPath) {
        List<String> sections = new PathUtil(this.state.isNestedKeys()).split(fullPath);
        Object[] nodes = new Object[sections.size() + 1];

        int pos = 0;
        TreeNode currentNode = (TreeNode) this.getRoot();
        nodes[pos] = currentNode;

        for(String section : sections) {
            pos++;
            currentNode = this.findNode(currentNode, section);
            nodes[pos] = currentNode;
        }

        return new TreePath(nodes);
    }

    public @Nullable DefaultMutableTreeNode findNode(@NotNull TreeNode parent, @NotNull String key) {
        for(int i = 0; i < parent.getChildCount(); i++) {
            TreeNode child = parent.getChildAt(i);

            if(child instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode mutableChild = (DefaultMutableTreeNode) child;
                String childKey = mutableChild.getUserObject().toString();

                if(mutableChild.getUserObject() instanceof PresentationData) {
                    childKey = ((PresentationData) mutableChild.getUserObject()).getPresentableText();
                }

                if(childKey != null && childKey.equals(key)) {
                    return mutableChild;
                }
            }
        }

        throw new NullPointerException("Cannot find node by key: " + key);
    }
}