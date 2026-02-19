package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper functions to split and join input {@link String strings} by an escapable delimiter.
 *
 * @author marhali
 */
public final class EscapableDelimiter {

    private static final char ESCAPE_CHAR = '\\';

    /**
     * Concatenates the provided values to a single {@link String} by joining the values by the specified delimiter.
     * If the delimiter occurs within any of the given values, the delimiter will be escaped.
     * @param values Split values
     * @param delimiter The delimiter
     * @return {@link String}
     */
    public static String joinByDelimiter(@NotNull List<@NotNull String> values, @Nullable String delimiter) {

        if (delimiter == null || delimiter.isEmpty()) {
            return String.join("", values);
        }

        return values.stream()
            .map(p -> escapeDelimiter(p, delimiter))
            .collect(Collectors.joining(delimiter));
    }

    /**
     * Splits the provided input {@link String} at the given delimiter.
     * Escaped delimiters within each split element will be unescaped.
     * @param input String to split
     * @param delimiter The delimiter
     * @return Split values
     */
    public static List<String> splitByDelimiter(@NotNull String input, @Nullable String delimiter) {

        if (delimiter == null || delimiter.isEmpty()) {
            return List.of(input);
        }

        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();

        int i = 0;
        while (i < input.length()) {
            // Escape sequence
            if (input.charAt(i) == ESCAPE_CHAR) {
                if (i + 1 >= input.length()) {
                    // Trailing "\" -> treat as literal
                    cur.append(ESCAPE_CHAR);
                    i++;
                    continue;
                }

                // Escaped delimiter
                if (input.startsWith(delimiter, i + 1)) {
                    cur.append(delimiter);
                    i += 1 + delimiter.length();
                    continue;
                }

                // Double escape sequence -> treat as literal
                if (input.charAt(i + 1) == ESCAPE_CHAR) {
                    cur.append(ESCAPE_CHAR);
                    i += 2;
                    continue;
                }

                // Generic escape "\x" -> "x"
                cur.append(input.charAt(i + 1));
                i += 2;
                continue;
            }

            // Unescaped delimiter -> split here
            if (input.startsWith(delimiter, i)) {
                out.add(cur.toString());
                cur.setLength(0);
                i += delimiter.length();
                continue;
            }

            // Any normal character
            cur.append(input.charAt(i));

            i++;
        }

        out.add(cur.toString());

        return out;
    }

    private static String escapeDelimiter(@NotNull String input, @NotNull String delimiter) {
        String escape_str = String.valueOf(ESCAPE_CHAR);

        return input
            .replace(escape_str, ESCAPE_CHAR + escape_str)
            .replace(delimiter, ESCAPE_CHAR + delimiter);
    }
}
