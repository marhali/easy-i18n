package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a hierarchical translation key within an i18n module.
 *
 * @param parts Translation key sections
 *
 * @author marhali
 */
public record I18nKey(
    @NotNull List<@NotNull String> parts) implements Comparable<I18nKey> {
    public I18nKey(List<String> parts) {
        this.parts = List.copyOf(parts);
    }

    public static I18nKey of(@NotNull String ...parts) {
        return new I18nKey(List.of(parts));
    }

    public static I18nKey of(@NotNull List<String> parts) {
        return new I18nKey(parts);
    }

    @Override
    public int compareTo(@NotNull I18nKey o) {
        int n = Math.min(this.parts.size(), o.parts.size());

        for (int i = 0; i < n; i++) {
            int c =  this.parts.get(i).compareTo(o.parts.get(i));
            if (c != 0) {
                return c;
            }
        }

        return Integer.compare(this.parts.size(), o.parts.size());
    }

    @Override
    public @NotNull String toString() {
        return "I18nKey{" +
            "parts=" + parts +
            '}';
    }
}
