package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Cached translation data. The data is stored in a tree structure.
 * Tree behaviour (sorted, non-sorted) can be specified via constructor.
 * For more please see {@link TranslationNode}. Example tree view:
 * <pre>
 * {@code
 * user:
 *     principal: 'Principal'
 *     username:
 *         title: 'Username'
 * auth:
 *     logout: 'Logout'
 *     login: 'Login'
 * }
 * </pre>
 * @author marhali
 */
public class TranslationData {

    @NotNull
    private final Set<String> locales;

    @NotNull
    private final TranslationNode rootNode;

    /**
     * Creates an empty instance.
     * @param sort Should the translation keys be sorted alphabetically
     */
    public TranslationData(boolean sort) {
        this(new HashSet<>(), new TranslationNode(sort ? new TreeMap<>() : new LinkedHashMap<>()));
    }

    /**
     * @param locales Languages which can be used for translation
     * @param rootNode Translation tree structure
     */
    public TranslationData(@NotNull Set<String> locales, @NotNull TranslationNode rootNode) {
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
     * @param locale Adds the provided locale to the supported languages list
     */
    public void addLocale(@NotNull String locale) {
        this.locales.add(locale);
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
    public @Nullable TranslationNode getNode(@NotNull KeyPath fullPath) {
        TranslationNode node = this.rootNode;

        if(fullPath.isEmpty()) { // Return root node if empty path was supplied
            return node;
        }

        for(String section : fullPath) {
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
    public @Nullable TranslationValue getTranslation(@NotNull KeyPath fullPath) {
        TranslationNode node = this.getNode(fullPath);

        if(node == null || !node.isLeaf()) {
            return null;
        }

        return node.getValue();
    }

    /**
     * Create / Update or Delete a specific translation.
     * The parent path of the translation will be changed if necessary.
     * @param fullPath Absolute translation key path
     * @param translation Translation to set. Can be null to delete the corresponding node
     */
    public void setTranslation(@NotNull KeyPath fullPath, @Nullable TranslationValue translation) {
        if(fullPath.isEmpty()) {
            throw new IllegalArgumentException("Key path cannot be empty");
        }

        fullPath = new KeyPath(fullPath);
        String leafKey = fullPath.remove(fullPath.size() - 1); // Extract edge section as children key of parent
        TranslationNode node = this.rootNode;

        for(String section : fullPath) { // Go to nested level at @leafKey
            TranslationNode childNode = node.getChildren().get(section);

            if(childNode == null) {
                if(translation == null) { // Path must not be empty on delete
                    throw new IllegalArgumentException("Delete action on empty path");
                }

                childNode = node.setChildren(section);
            }

            node = childNode;
        }

        if(translation == null) { // Delete action
            node.removeChildren(leafKey);

            if(node.getChildren().isEmpty() && !node.isRoot()) { // Node is empty now. Run delete recursively
                this.setTranslation(fullPath, null);
            }
            return;
        }

        // Create or overwrite
        node.setChildren(leafKey, translation);
    }

    /**
     * @return All translation keys as absolute paths (full-key)
     */
    public @NotNull Set<KeyPath> getFullKeys() {
        return this.getFullKeys(new KeyPath(), this.rootNode); // Just use root node
    }

    /**
     * @param parentPath Parent key path
     * @param node Node section to begin with
     * @return All translation keys where the path contains the specified @parentPath
     */
    public @NotNull Set<KeyPath> getFullKeys(KeyPath parentPath, TranslationNode node) {
        Set<KeyPath> keys = new LinkedHashSet<>();

        if(node.isLeaf()) { // This node does not lead to child's - just add the key
            keys.add(parentPath);
        }

        for(Map.Entry<String, TranslationNode> children : node.getChildren().entrySet()) {
            keys.addAll(this.getFullKeys(new KeyPath(parentPath, children.getKey()), children.getValue()));
        }

        return keys;
    }

    @Override
    public String toString() {
        return "TranslationData{" +
                "mapClass=" + rootNode.getChildren().getClass().getSimpleName() +
                ", locales=" + locales +
                ", rootNode=" + rootNode +
                '}';
    }
}