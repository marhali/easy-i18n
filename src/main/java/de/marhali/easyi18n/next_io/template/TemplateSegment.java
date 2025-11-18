package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a single section from a template syntax string.
 *
 * @author marhali
 */
public class TemplateSegment {

    /**
     * Shorthand to construct a plaintext section.
     * @param literal Plaintext literal
     * @return {@link LiteralTemplateSegment}
     */
    protected static LiteralTemplateSegment fromLiteral(String literal) {
        return new LiteralTemplateSegment(literal);
    }

    /**
     * Shorthand to construct a parameter section.
     * @param name Parameter name
     * @param constraint Optional parameter regex constraint
     * @return {@link ParameterTemplateSegment}
     */
    protected static ParameterTemplateSegment fromParameter(String name, @Nullable String constraint) {
        return new ParameterTemplateSegment(name, constraint);
    }

    protected TemplateSegment() {}

    /**
     * @return {@code true} if this section is a plaintext literal, otherwise {@code false}
     */
    public boolean isLiteral() {
        return this instanceof LiteralTemplateSegment;
    }

    /**
     * @return {@link LiteralTemplateSegment}
     */
    public LiteralTemplateSegment getAsLiteral() {
        return (LiteralTemplateSegment) this;
    }

    /**
     * @return {@code true} if this section is a parameter, otherwise {@code false}
     */
    public boolean isParameter() {
        return this instanceof ParameterTemplateSegment;
    }

    /**
     * @return {@link ParameterTemplateSegment}
     */
    public ParameterTemplateSegment getAsParameter() {
        return (ParameterTemplateSegment) this;
    }
}
