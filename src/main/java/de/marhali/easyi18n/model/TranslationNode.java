package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Translation tree node. Manages child nodes which can be translations or also
 * nodes which can lead to another translation or node.<br>
 * Navigation inside a node can be upward and downward. To construct the full
 * translation key (full-key) every parent needs to be resolved recursively. <br>
 * <br>
 * Whether the children nodes should be sorted is determined by the parent node.
 * For root nodes (empty parent) the {@link java.util.Map}-Type must be specified
 * to determine which sorting should be applied.
 * @author marhali
 */
public class TranslationNode {

    @Nullable
    private TranslationNode parent;

    @NotNull
    private Map<String, TranslationNode> children;

    @NotNull
    private TranslationValue value;

    public TranslationNode(boolean sort) {
        this(sort ? new TreeMap<>() : new LinkedHashMap<>());
    }

    /**
     * Root node initializer. E.g. see {@link java.util.TreeMap} or {@link java.util.HashMap}
     * @param children Decide which implementation should be used (sorted, non-sorted)
     */
    public TranslationNode(@NotNull Map<String, TranslationNode> children) {
        this.parent = null;
        this.children = children;
        this.value = new TranslationValue();
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

    public @NotNull TranslationValue getValue() {
        return value;
    }

    public void setValue(@NotNull TranslationValue value) {
        this.children.clear();
        this.value = value;
    }

    public @NotNull Map<String, TranslationNode> getChildren() {
        return this.children;
    }

    public void setChildren(@NotNull String key, @NotNull TranslationNode node) {
        node.setParent(this); // Track parent if adding children's
        this.value.clear();
        this.children.put(key, node);
    }

    @SuppressWarnings("unchecked")
    public @NotNull TranslationNode setChildren(@NotNull String key) {
        try {
            TranslationNode node = new TranslationNode(this.children.getClass().getDeclaredConstructor().newInstance());
            this.setChildren(key, node);
            return node;
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot create children of map type " + this.children.getClass().getSimpleName());
        }
    }

    public void setChildren(@NotNull String key, @NotNull TranslationValue translation) {
        this.setChildren(key).setValue(translation);
    }

    public @NotNull TranslationNode getOrCreateChildren(@NotNull String key) {
        TranslationNode node = this.children.get(key);

        if(node == null) {
            node = this.setChildren(key);
        }

        return node;
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