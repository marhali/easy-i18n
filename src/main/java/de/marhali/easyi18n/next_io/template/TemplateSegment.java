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
     * @param delimiter Optional parameter delimiter
     * @param constraint Optional parameter constraint
     * @return {@link ParameterTemplateSegment}
     */
    protected static ParameterTemplateSegment fromParameter(String name, @Nullable String delimiter, @Nullable String constraint) {
        return new ParameterTemplateSegment(name, delimiter, constraint);
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

    @Override
    public String toString() {
        if (isLiteral()) {
            return this.getAsLiteral().toString();
        } else if (isParameter()) {
            return this.getAsParameter().toString();
        } else {
            throw new UnsupportedOperationException("Unknown template segment: " + this.getClass().getSimpleName());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (isLiteral()) {
            return this.getAsLiteral().equals(obj);
        } else if (isParameter()) {
            return this.getAsParameter().equals(obj);
        } else {
            throw new UnsupportedOperationException("Unknown template segment: " + this.getClass().getSimpleName());
        }
    }
}
