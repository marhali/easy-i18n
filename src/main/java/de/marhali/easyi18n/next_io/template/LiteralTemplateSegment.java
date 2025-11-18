package de.marhali.easyi18n.next_io.template;

import java.util.Objects;

/**
 * Represents a plaintext template section.
 *
 * @author marhali
 */
public class LiteralTemplateSegment extends TemplateSegment {

    /**
     * Plaintext literal that represents this section.
     */
    private final String literal;

    protected LiteralTemplateSegment(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return "LiteralTemplateSegment{" +
            "literal='" + literal + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LiteralTemplateSegment that = (LiteralTemplateSegment) o;
        return Objects.equals(literal, that.literal);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(literal);
    }
}
