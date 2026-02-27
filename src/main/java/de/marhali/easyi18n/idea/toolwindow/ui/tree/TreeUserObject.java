package de.marhali.easyi18n.idea.toolwindow.ui.tree;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User object for tree nodes.
 *
 * @author marhali
 */
public sealed interface TreeUserObject permits TreeUserObject.Node, TreeUserObject.Leaf {

    /**
     * @return {@code true} if this user object is a leaf node, otherwise {@code false}.
     */
    default boolean isLeaf() {
        return this instanceof Leaf;
    }

    /**
     * @return {@link Node}
     */
    default @NotNull Node getAsNode() {
        return (Node) this;
    }

    /**
     * @return {@link Leaf}
     */
    default @NotNull Leaf getAsLeaf() {
        return (Leaf) this;
    }

    /**
     * Normal tree node.
     * @param name Hierarchical translation key segment
     * @param missingValues Indicator if translation has missing translation values
     */
    record Node(
        @NotNull String name,
        boolean missingValues
    ) implements TreeUserObject { }

    /**
     * Leaf tree node.
     * @param key I18n key
     * @param localeId Locale identifier
     * @param value I18n value
     * @param duplicatedValue Indicator if value is duplicated to others
     */
    record Leaf(
        @NotNull I18nKey key,
        @NotNull LocaleId localeId,
        @Nullable I18nValue value,
        boolean duplicatedValue
        ) implements TreeUserObject { }
}
