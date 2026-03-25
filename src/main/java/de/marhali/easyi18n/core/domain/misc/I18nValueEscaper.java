package de.marhali.easyi18n.core.domain.misc;

import org.jetbrains.annotations.NotNull;

/**
 * Helper to escape und unescape translation values.
 *
 * @author marhali
 */
public final class I18nValueEscaper {
    private I18nValueEscaper() {}

    /**
     * Escapes control characters
     * @param input Input string
     * @return Escaped input string
     */
    public static @NotNull String escape(@NotNull String input) {
        return input
            .replace("\\", "\\\\")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Unescapes escaped control characters.
     * @param input Input string
     * @return Unescaped input string
     */
    public static @NotNull String unescape(@NotNull String input) {
        return input.translateEscapes();
    }
}
