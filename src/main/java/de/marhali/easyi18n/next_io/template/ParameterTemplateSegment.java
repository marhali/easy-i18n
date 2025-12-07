package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Represents a template section that consists of a parameter with an optional delimiter and constraint.
 *
 * @author marhali
 */
public class ParameterTemplateSegment extends TemplateSegment {

    /**
     * The parameter name.
     */
    private final String name;

    /**
     * Optional delimiter to apply on this parameter.
     */
    private final @Nullable String delimiter;

    /**
     * Optional constraint to apply on this parameter.
     */
    private final @Nullable String constraint;

    protected ParameterTemplateSegment(
        String name,
        @Nullable String delimiter,
        @Nullable String constraint
    ) {
        this.name = name;
        this.delimiter = delimiter;
        this.constraint = constraint;
    }

    public String getName() {
        return name;
    }

    public boolean hasDelimiter() {
        return delimiter != null;
    }

    public @Nullable String getDelimiter() {
        return delimiter;
    }

    public boolean hasConstraint() {
        return constraint != null;
    }

    public @Nullable String getConstraint() {
        return constraint;
    }

    public @NotNull List<String> splitByDelimiter(@NotNull String value) {
        if (delimiter == null) {
            return List.of(value);
        }

        return List.of(value.split(delimiter));
    }

    @Override
    public String toString() {
        return "ParameterTemplateSegment{" +
            "name='" + name + '\'' +
            ", delimiter='" + delimiter + '\'' +
            ", constraint='" + constraint + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParameterTemplateSegment that = (ParameterTemplateSegment) o;
        return Objects.equals(name, that.name) && Objects.equals(delimiter, that.delimiter) && Objects.equals(constraint, that.constraint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, delimiter, constraint);
    }
}
