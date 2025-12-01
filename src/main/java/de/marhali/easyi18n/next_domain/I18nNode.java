package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author marhali
 */
public class I18nNode {

    @Nullable
    private I18nNode parent;

    @Nullable
    private Map<String, I18nNode> children;

    @Nullable
    private I18nValue value;

    /**
     * Checks whether this node has a parent node or is the root node.
     * @return {@code true} if this is the root node, otherwise {@code false}
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks whether this node has child nodes associated or not.
     * @return {@code true} if this node has no child nodes, otherwise {@code false}
     */
    public boolean isLeaf() {
        return children == null;
    }
}
