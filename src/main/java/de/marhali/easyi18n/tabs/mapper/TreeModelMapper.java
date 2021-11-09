package de.marhali.easyi18n.tabs.mapper;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ui.JBColor;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.model.bus.SearchQueryListener;
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
public class TreeModelMapper extends DefaultTreeModel implements SearchQueryListener {

    private final TranslationData data;
    private final SettingsState state;

    public TreeModelMapper(TranslationData data, SettingsState state) {
        super(null);

        this.data = data;
        this.state = state;

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        this.generateNodes(rootNode, this.data.getRootNode(), null);
        super.setRoot(rootNode);
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        this.generateNodes(rootNode, this.data.getRootNode(), query);
        super.setRoot(rootNode);
    }

    private void generateNodes(@NotNull DefaultMutableTreeNode parent,
                               @NotNull TranslationNode translationNode, @Nullable String searchQuery) {
        for(Map.Entry<String, TranslationNode> entry : translationNode.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childTranslationNode = entry.getValue();

            if(searchQuery != null) {
                searchQuery = searchQuery.toLowerCase();
                if(!this.isApplicable(key, childTranslationNode, searchQuery)) {
                    continue;
                }
            }

            if(!childTranslationNode.isLeaf()) {
                // Nested node - run recursively
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(key);
                this.generateNodes(childNode, childTranslationNode, searchQuery);
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

    /**
     * Checks if the provided tree (@node) is applicable for the search string.
     * A full-text-search is applied and section keys and every value will be evaluated.
     * @param key Section key
     * @param node Node which has @key as key
     * @param searchQuery Search query to search for
     * @return True if this node or ANY child is relevant for the search context
     */
    private boolean isApplicable(@NotNull String key, @NotNull TranslationNode node, @NotNull String searchQuery) {
        if(key.toLowerCase().contains(searchQuery)) {
            return true;
        }

        if(!node.isLeaf()) {
            for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
                if(this.isApplicable(entry.getKey(), entry.getValue(), searchQuery)) {
                    return true;
                }
            }
        } else {
            for(String content : node.getValue().values()) {
                if(content.toLowerCase().contains(searchQuery)) {
                    return true;
                }
            }
        }

        return false;
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