package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a template section that consists of a parameter with an optional regex constraint.
 *
 * @author marhali
 */
public class ParameterTemplateSegment extends TemplateSegment {

    /**
     * The parameter name.
     */
    private final String name;

    /**
     * Optional regex constraint to apply on this parameter.
     */
    @Nullable
    private final String constraint;

    protected ParameterTemplateSegment(String name, @Nullable String constraint) {
        this.name = name;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public @Nullable String getConstraint() {
        return constraint;
    }

    @Override
    public String toString() {
        return "ParameterTemplateSegment{" +
            "name='" + name + '\'' +
            ", constraint='" + constraint + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParameterTemplateSegment that = (ParameterTemplateSegment) o;
        return Objects.equals(name, that.name) && Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, constraint);
    }
}
