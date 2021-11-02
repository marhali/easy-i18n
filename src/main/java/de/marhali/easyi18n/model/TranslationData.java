package de.marhali.easyi18n.model;

import de.marhali.easyi18n.util.PathUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Cached translation data. The data is stored in a tree structure.
 * Tree behaviour (sorted, non-sorted) can be specified via constructor.
 * For more please see {@link TranslationNode}. Example tree view:
 *
 *  #################################
 *  # - user:                       #
 *  #     - principal: 'Principal'  #
 *  #     - username:               #
 *  #         - title: 'Username'   #
 *  # - auth:                       #
 *  #     - logout: 'Logout'        #
 *  #     - login: 'Login'          #
 *  #################################
 *
 * @author marhali
 */
public class TranslationData {

    private final PathUtil pathUtil;

    @NotNull
    private final Set<String> locales;

    @NotNull
    private final TranslationNode rootNode;

    /**
     * Creates an empty instance.
     * @param sort Should the translation keys be sorted alphabetically
     */
    public TranslationData(boolean sort, boolean nestKeys) {
        this(nestKeys, new HashSet<>(), new TranslationNode(sort ? new TreeMap<>() : new LinkedHashMap<>()));
    }

    /**
     * @param nestKeys Apply key nesting. See {@link PathUtil}
     * @param locales Languages which can be used for translation
     * @param rootNode Translation tree structure
     */
    public TranslationData(boolean nestKeys, @NotNull Set<String> locales, @NotNull TranslationNode rootNode) {
        this.pathUtil = new PathUtil(nestKeys);
        this.locales = locales;
        this.rootNode = rootNode;
    }

    /**
     * @return Set of languages which can receive translations
     */
    public @NotNull Set<String> getLocales() {
        return this.locales;
    }

    /**
     * @return root node which contains all translations
     */
    public @NotNull TranslationNode getRootNode() {
        return this.rootNode;
    }

    /**
     * @param fullPath Absolute translation path
     * @return Translation node which leads to translations or nested child's
     */
    public @Nullable TranslationNode getNode(@NotNull String fullPath) {
        List<String> sections = this.pathUtil.split(fullPath);
        TranslationNode node = this.rootNode;

        if(fullPath.isEmpty()) { // Return root node if empty path was supplied
            return node;
        }

        for(String section : sections) {
            if(node == null) {
                return null;
            }
            node = node.getChildren().get(section);
        }

        return node;
    }

    /**
     * @param fullPath Absolute translation key path
     * @return Found translation. Can be null if path is empty or is not a leaf element
     */
    public @Nullable Translation getTranslation(@NotNull String fullPath) {
        TranslationNode node = this.getNode(fullPath);

        if(node == null || !node.isLeaf()) {
            return null;
        }

        return node.getValue();
    }

    /**
     * @param fullPath Absolute translation key path
     * @param translation Translation to set. Can be null to delete the corresponding node
     */
    public void setTranslation(@NotNull String fullPath, @Nullable Translation translation) throws Exception {
        List<String> sections = this.pathUtil.split(fullPath);
        String nodeKey = sections.remove(sections.size() - 1); // Edge case last section
        TranslationNode node = this.rootNode;

        if(fullPath.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be empty");
        }

        for(String section : sections) { // Go to the level of the key (@nodeKey)
            TranslationNode childNode = node.getChildren().get(section);

            if(childNode == null) {
                if(translation == null) { // Path should not be empty for delete
                    throw new IllegalArgumentException("Delete action on empty path");
                }

                // Created nested section
                childNode = node.addChildren(section);
            }

            node = childNode;
        }

        if(translation == null) { // Delete
            node.removeChildren(nodeKey);

            if(node.getChildren().isEmpty() && !node.isRoot()) { // Parent is empty now. Run delete recursively
                this.setTranslation(this.pathUtil.concat(sections), null);
            }

        } else { // Create or overwrite
            node.addChildren(nodeKey, translation);
        }
    }

    /**
     * @return All translation keys as absolute paths (full-key)
     */
    public @NotNull Set<String> getFullKeys() {
        return this.getFullKeys("", this.rootNode); // Just use root node
    }

    /**
     * @param parentPath Parent key path
     * @param node Node section to begin with
     * @return All translation keys where the path contains the specified @parentPath
     */
    public @NotNull Set<String> getFullKeys(String parentPath, TranslationNode node) {
        Set<String> keys = new LinkedHashSet<>();

        if(node.isLeaf()) { // This node does not lead to child's - just add the key
            keys.add(parentPath);
        }

        for(Map.Entry<String, TranslationNode> children : node.getChildren().entrySet()) {
            keys.addAll(this.getFullKeys(this.pathUtil.append(parentPath, children.getKey()), children.getValue()));
        }

        return keys;
    }

    @Override
    public String toString() {
        return "TranslationData{" +
                "mapClass=" + rootNode.getChildren().getClass().getSimpleName() +
                ", pathUtil=" + pathUtil +
                ", locales=" + locales +
                ", rootNode=" + rootNode +
                '}';
    }
}