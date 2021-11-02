package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Translation tree node. Manages child nodes which can be translations or also
 * nodes which can lead to another translation or node.
 * Navigation inside a node can be upward and downward. To construct the full
 * translation key (full-key) every parent needs to be resolved recursively.
 * -
 * Whether the children nodes should be sorted is determined by the parent node.
 * For root nodes (empty parent) the {@link java.util.Map}-Type must be specified
 * to determine which sorting should be applied.
 *
 * @author marhali
 */
public class TranslationNode {

    @Nullable
    private TranslationNode parent;

    @NotNull
    private Map<String, TranslationNode> children;

    @NotNull
    private Translation value;

    /**
     * Root node initializer. E.g. see {@link java.util.TreeMap} or {@link java.util.HashMap}
     * @param children Decide which implementation should be used (sorted, non-sorted)
     */
    public TranslationNode(@NotNull Map<String, TranslationNode> children) {
        this.parent = null;
        this.children = children;
        this.value = new Translation();
    }

    /**
     * @return true if this node is considered as root node
     */
    public boolean isRoot() {
        return this.parent == null;
    }

    /**
     * @return true if this node does not lead to other children nodes (just contains {@link Translation} itself).
     *          The root node is never treated as a leaf node.
     */
    public boolean isLeaf() {
        return this.children.isEmpty() && !this.isRoot();
    }

    public void setParent(@Nullable TranslationNode parent) {
        this.parent = parent;
    }

    public @NotNull Translation getValue() {
        return value;
    }

    public void setValue(@NotNull Translation value) {
        this.children.clear();
        this.value = value;
    }

    public @NotNull Map<String, TranslationNode> getChildren() {
        return this.children;
    }

    public void addChildren(@NotNull String key, @NotNull TranslationNode node) {
        node.setParent(this); // Track parent if adding children's
        this.value.clear();
        this.children.put(key, node);
    }

    public TranslationNode addChildren(@NotNull String key) throws Exception {
        TranslationNode node = new TranslationNode(this.children.getClass().getDeclaredConstructor().newInstance());
        this.addChildren(key, node);
        return node;
    }

    public void addChildren(@NotNull String key, @NotNull Translation translation) throws Exception {
        this.addChildren(key).setValue(translation);
    }

    public void removeChildren(@NotNull String key) {
        this.children.remove(key);
    }

    @Override
    public String toString() {
        return "TranslationNode{" +
                "parent=" + parent +
                ", children=" + children.keySet() +
                ", value=" + value +
                '}';
    }
}