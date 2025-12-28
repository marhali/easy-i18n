package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a single value from a template syntax string.
 *
 * @author marhali
 */
public abstract class TemplateValue {

    /**
     * Shorthand to construct a fixed literal value
     *
     * @param text Plaintext literal
     * @return {@link LiteralTemplateValue}
     */
    public static @NotNull LiteralTemplateValue fromLiteral(@NotNull String text) {
        return new LiteralTemplateValue(text);
    }

    /**
     * Shorthand to construct a parameter value.
     *
     * @param name  Parameter name
     * @param value Actual parameter value
     * @return {@link ParameterTemplateValue}
     */
    public static @NotNull ParameterTemplateValue fromParameter(@NotNull String name, @NotNull String value) {
        return new ParameterTemplateValue(name, value);
    }

    /**
     * @return {@code true} if this section is a plaintext literal, otherwise {@code false}
     */
    public boolean isLiteral() {
        return this instanceof LiteralTemplateValue;
    }

    /**
     * @return {@link LiteralTemplateValue}
     */
    public LiteralTemplateValue getAsLiteral() {
        return (LiteralTemplateValue) this;
    }

    /**
     * @return {@code true} if this value is a parameter, otherwise {@code false}
     */
    public boolean isParameter() {
        return this instanceof ParameterTemplateValue;
    }

    /**
     * @return {@link ParameterTemplateValue}
     */
    public ParameterTemplateValue getAsParameter() {
        return (ParameterTemplateValue) this;
    }

    @Override
    public String toString() {
        if (isLiteral()) {
            return this.getAsLiteral().toString();
        } else if (isParameter()) {
            return this.getAsParameter().toString();
        } else {
            throw new UnsupportedOperationException("Unknown template value: " + this.getClass().getSimpleName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (isLiteral()) {
            return this.getAsLiteral().equals(obj);
        } else if (isParameter()) {
            return this.getAsParameter().equals(obj);
        } else {
            throw new UnsupportedOperationException("Unknown template value: " + this.getClass().getSimpleName());
        }
    }
}
