package de.marhali.easyi18n.next_io.template;

import java.util.Objects;

/**
 * @author marhali
 */
public class LiteralTemplateValue extends TemplateValue {

    /**
     * Associated literal value.
     */
    private final String text;

    protected LiteralTemplateValue(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "LiteralTemplateValue{" +
            "text='" + text + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LiteralTemplateValue that = (LiteralTemplateValue) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }
}
