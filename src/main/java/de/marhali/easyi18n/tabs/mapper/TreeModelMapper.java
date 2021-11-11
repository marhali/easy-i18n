package de.marhali.easyi18n.tabs.mapper;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ui.JBColor;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.model.bus.SearchQueryListener;
import de.marhali.easyi18n.util.PathUtil;
import de.marhali.easyi18n.util.UiUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.*;
import java.util.ArrayList;
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
        this.generateNodes(rootNode, this.data.getRootNode());
        super.setRoot(rootNode);
    }

    @Override
    public void onSearchQuery(@Nullable String query) {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
        TranslationData shadow = new TranslationData(this.state.isSortKeys(), this.state.isNestedKeys());

        if(query == null) {
            this.generateNodes(rootNode, this.data.getRootNode());
            super.setRoot(rootNode);
            return;
        }

        query = query.toLowerCase();

        for(String currentKey : this.data.getFullKeys()) {
            Translation translation = this.data.getTranslation(currentKey);
            String loweredKey = currentKey.toLowerCase();

            if(query.contains(loweredKey) || loweredKey.contains(query)) {
                shadow.setTranslation(currentKey, translation);
                continue;
            }

            for(String currentContent : translation.values()) {
                if(currentContent.toLowerCase().contains(query)) {
                    shadow.setTranslation(currentKey, translation);
                    break;
                }
            }
        }

        this.generateNodes(rootNode, shadow.getRootNode());
        super.setRoot(rootNode);
    }

    private void generateNodes(@NotNull DefaultMutableTreeNode parent, @NotNull TranslationNode translationNode) {
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
        List<Object> nodes = new ArrayList<>();

        TreeNode currentNode = (TreeNode) this.getRoot();
        nodes.add(currentNode);

        for(String section : sections) {
            currentNode = this.findNode(currentNode, section);

            if(currentNode == null) {
                break;
            }

            nodes.add(currentNode);
        }

        return new TreePath(nodes.toArray());
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

        return null;
    }
}