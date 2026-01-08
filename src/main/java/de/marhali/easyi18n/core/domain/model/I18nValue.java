package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Single localized translation value. A translation value can be either:
 * <ul>
 *     <li>{@link I18nValue.Primitive}: {@link Bare} or {@link Quoted}</li>
 *     <li>{@link I18nValue.Array}: consisting of primitive elements</li>
 * </ul>
 *
 * @author marhali
 */
public sealed interface I18nValue permits I18nValue.Primitive, I18nValue.Array {

    /**
     * Marker for primitive translation values.
     */
    sealed interface Primitive extends I18nValue permits I18nValue.Bare, I18nValue.Quoted {
        default boolean isBare() {
            return this instanceof Bare;
        }

        default boolean isQuoted() {
            return this instanceof Quoted;
        }

        default @NotNull String getText() {
            return isBare() ? ((Bare) this).text : ((Quoted) this).text;
        }
    }

    /**
     * @param text Bare translation value text
     */
    record Bare(@NotNull String text) implements I18nValue.Primitive {
        @Override
        public @NotNull String toInputString() {
            return text;
        }

        @Override
        public @NotNull String toString() {
            return "Bare{" +
                "text='" + text + '\'' +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Bare bare = (Bare) o;
            return Objects.equals(text, bare.text);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(text);
        }
    }

    /**
     * @param text Quoted translation value text
     */
    record Quoted(@NotNull String text) implements I18nValue.Primitive {
        @Override
        public @NotNull String toInputString() {
            return "\"" + text + "\"";
        }

        @Override
        public @NotNull String toString() {
            return "Quoted{" +
                "text='" + text + '\'' +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Quoted quoted = (Quoted) o;
            return Objects.equals(text, quoted.text);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(text);
        }
    }

    /**
     * @param elements Primitive translation value elements
     */
    record Array(@NotNull I18nValue.Primitive[] elements) implements I18nValue {
        @Override
        public @NotNull String toInputString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");

            for (int i = 0; i < elements.length; i++) {
                builder.append(elements[i].toInputString());
                if (i < elements.length - 1) {
                    builder.append("; ");
                }
            }

            builder.append("]");
            return builder.toString();
        }

        @Override
        public @NotNull String toString() {
            return "Array{" +
                "elements=" + Arrays.toString(elements) +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Array array = (Array) o;
            return Objects.deepEquals(elements, array.elements);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(elements);
        }
    }

    /**
     * Constructs a bare primitive from the provided text.
     * @param text Bare translation value text
     * @return {@link I18nValue.Bare}
     */
    static @NotNull Bare fromBarePrimitive(@NotNull String text) {
        return new Bare(text);
    }

    /**
     * Constructs a quoted primitive from the provided text.
     * @param text Quoted translation value text
     * @return {@link I18nValue.Quoted}
     */
    static @NotNull Quoted fromQuotedPrimitive(@NotNull String text) {
        return new Quoted(text);
    }

    /**
     * Constructs an array of primitive elements.
     * @param elements Primitive translation value elements
     * @return {@link I18nValue.Array}
     */
    static @NotNull Array fromArray(@NotNull Primitive ...elements) {
        return new Array(elements);
    }

    /**
     * Helper function to parse the translation value from an input string.
     * @param input Input string
     * @return {@link I18nValue}
     */
    static @NotNull I18nValue fromInputString(@NotNull String input) {
        // Someone might be missing trailing or leading whitespace - but for that cases use quoted primitives
        input = input.trim();

        // Array
        if (input.startsWith("[") && input.endsWith("]")) {
            var elements = Arrays.stream(input.substring(1, input.length() - 1).split(";"))
                .map(I18nValue::fromInputString)
                .filter(I18nValue::isPrimitive)
                .map(I18nValue::getAsPrimitive)
                .toArray(Primitive[]::new);

            return fromArray(elements);
        }

        // Quoted primitive
        if (input.startsWith("\"") && input.endsWith("\"")) {
            return fromQuotedPrimitive(input.substring(1, input.length() - 1));
        }

        // Bare primitive
        return fromBarePrimitive(input);
    }

    /**
     * Helper function to transform this value into an input string.
     * @return {@link String}
     */
    @NotNull String toInputString();

    /**
     * Indicates whether this value is a primitive.
     * @return true if primitive otherwise false
     */
    default boolean isPrimitive() {
        return this instanceof I18nValue.Primitive;
    }

    /**
     * Retrieves this value as a primitive.
     * @return {@link Primitive}
     */
    default @NotNull Primitive getAsPrimitive() {
        return (Primitive) this;
    }

    /**
     * Indicates whether this value is an array of primitive elements.
     * @return true if array otherwise false
     */
    default boolean isArray() {
        return this instanceof I18nValue.Array;
    }

    /**
     * Retrieves this value as an array of primitive elements.
     * @return {@link Array}
     */
    default @NotNull Array getAsArray() {
        return (Array) this;
    }
}
