package de.marhali.easyi18n.util;

import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;

/**
 * String utilities
 * @author marhali, Apache Commons
 */
public class StringUtil {

    /**
     * Escapes control characters for the given input string.
     * Inspired by Apache Commons (see {@link org.apache.commons.lang.StringEscapeUtils}
     * @param input The input string
     * @param skipStrings Should every string literal indication ("", '') be skipped? (Needed e.g. for json)
     * @return Escaped string
     */
    public static @NotNull String escapeControls(@NotNull String input, boolean skipStrings) {
        int length = input.length();
        StringWriter out = new StringWriter(length * 2);

        for(int i = 0; i < length; i++) {
            char ch = input.charAt(i);

            if(ch < ' ') {
                switch(ch) {
                    case '\b':
                        out.write(92);
                        out.write(98);
                        break;
                    case '\t':
                        out.write(92);
                        out.write(116);
                        break;
                    case '\n':
                        out.write(92);
                        out.write(110);
                        break;
                    case '\u000b':
                    default:
                        if (ch > 15) {
                            out.write("\\u00" + hex(ch));
                        } else {
                            out.write("\\u000" + hex(ch));
                        }
                        break;
                    case '\f':
                        out.write(92);
                        out.write(102);
                        break;
                    case '\r':
                        out.write(92);
                        out.write(114);
                }
            } else {
                switch(ch) {
                    case '"':
                        if(!skipStrings) {
                            out.write(92);
                        }
                        out.write(34);
                        break;
                    case '\'':
                        if(!skipStrings) {
                            out.write(92);
                        }
                        out.write(39);
                        break;
                    case '/':
                        out.write(92);
                        out.write(47);
                        break;
                    case '\\':
                        out.write(92);
                        out.write(92);
                        break;
                    default:
                        out.write(ch);
                }
            }
        }

        return out.toString();
    }

    private static @NotNull String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase();
    }
}