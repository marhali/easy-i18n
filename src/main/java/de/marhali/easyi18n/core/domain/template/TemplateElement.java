package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a single element from a template syntax string.
 *
 * @author marhali
 */
public sealed interface TemplateElement permits TemplateElement.Literal, TemplateElement.Placeholder {

    /**
     * Shorthand to construct a plaintext template element.
     *
     * @param text Plaintext literal
     * @return {@link Literal}
     */
    static Literal fromLiteral(@NotNull String text) {
        return new Literal(text);
    }

    /**
     * Shorthand to construct a placeholder template element.
     *
     * @param name Parameter name
     * @param delimiter Optional delimiter to apply
     * @param constraint Optional constraint to apply
     * @return {@link Placeholder}
     */
    static Placeholder fromPlaceholder(@NotNull String name, @Nullable String delimiter, @Nullable String constraint) {
        return new Placeholder(name, delimiter, constraint);
    }

    /**
     * Template element that consists of a literal text.
     *
     * @param text Plaintext literal
     */
    record Literal(
        @NotNull String text
    ) implements TemplateElement {}


    /**
     * Template element that consists of a placeholder value.
     *
     * @param name The placeholder name
     * @param delimiter Optional delimiter to apply
     * @param constraint Optional constraint to apply
     */
    record Placeholder(
        @NotNull String name,
        @Nullable String delimiter,
        @Nullable String constraint
    ) implements TemplateElement {
        /**
         * @return {@code true} if delimiter is set, otherwise {@code false}
         */
        public boolean hasDelimiter() {
            return delimiter != null;
        }

        /**
         * @return {@code true} if constraint is set, otherwise {@code false}
         */
        public boolean hasConstraint() {
            return constraint != null;
        }

        /**
         * Splits the provided input string by the set delimiter.
         * @param input Input string
         * @return List of split input string if delimiter is set, otherwise a list with the input string as a single element
         */
        public @NotNull List<@NotNull String> splitByDelimiter(@NotNull String input) {
            return EscapableDelimiter.splitByDelimiter(input, delimiter);
        }

        public @NotNull String joinByDelimiter(@NotNull List<@NotNull String> values) {
            return EscapableDelimiter.joinByDelimiter(values, delimiter);
        }
    }

    /**
     * Checks whether this template element is a plaintext literal or not.
     * @return {@code true} if instanceof of {@link Literal}, otherwise {@code false}
     */
    default boolean isLiteral() {
        return this instanceof Literal;
    }

    /**
     * @return {@link Literal}
     */
    default @NotNull Literal getAsLiteral() {
        return (Literal) this;
    }

    /**
     * Checks whether this template element is a placeholder.
     * @return {@code true} if instanceof of {@link Placeholder}, otherwise {@code false}
     */
    default boolean isPlaceholder() {
        return this instanceof Placeholder;
    }

    /**
     * @return {@link Placeholder}
     */
    default @NotNull Placeholder getAsPlaceholder() {
        return (Placeholder) this;
    }
}
