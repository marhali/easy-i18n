package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

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
    private final @NotNull String text;

    protected LiteralTemplateSegment(@NotNull String text) {
        this.text = text;
    }

    public @NotNull String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "LiteralTemplateSegment{" +
            "text='" + text + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LiteralTemplateSegment that = (LiteralTemplateSegment) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(text);
    }
}
