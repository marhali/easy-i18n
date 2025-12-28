package de.marhali.easyi18n.next_io.template;

import java.util.Objects;

/**
 * @author marhali
 */
public class ParameterTemplateValue extends TemplateValue {

    /**
     * Parameter identifier name.
     */
    private final String name;

    /**
     * Actual parameter value
     */
    private final String value;

    protected ParameterTemplateValue(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ParameterTemplateValue{" +
            "name='" + name + '\'' +
            ", value='" + value + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ParameterTemplateValue that = (ParameterTemplateValue) o;
        return Objects.equals(name, that.name) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
