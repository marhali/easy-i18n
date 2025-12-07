package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser for template syntax strings.
 *
 * @author marhali
 */
public class TemplateParser {

    /**
     * Regex pattern to match parameter names. Must be compliant to be used for regex named group.
     */
    private static final @NotNull Pattern PARAM_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    /**
     * Parses the provided template string into template segments.
     *
     * @param template The template syntax string
     * @return List of parsed segments left-to-right
     */
    public static @NotNull List<TemplateSegment> parseSegments(@NotNull String template) {
        List<TemplateSegment> segments = new ArrayList<>();
        StringBuilder literal = new StringBuilder();

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            if (c == '{') {
                if (i > 0 && template.charAt(i - 1) == '\\') {
                    // \{ -> Escaped parameter segment, we will ignore it from parsing
                    literal.deleteCharAt(literal.length() - 1);
                    literal.append(c);
                    continue;
                }

                if (!literal.isEmpty()) {
                    // Flush any existing literal before continuing with a parameter segment
                    segments.add(new LiteralTemplateSegment(literal.toString()));
                    literal.setLength(0);
                }

                int end = template.indexOf('}', i + 1);

                if (end == -1) {
                    throw new IllegalArgumentException("Missing closing parameter segment ('}') in template '" + template + "'");
                }

                String inside = template.substring(i + 1, end);

                String[] insideSplit = inside.split(":", 3);

                String parameterName = insideSplit[0];
                String optionalParameterDelimiter = null;
                String optionalParameterConstraint = null;

                // Extract parameter delimiter if set and not blank
                if (insideSplit.length > 1 && !insideSplit[1].isEmpty()) {
                    optionalParameterDelimiter = insideSplit[1];
                }

                // Extract parameter constraint if set and not blank
                if (insideSplit.length > 2 && !insideSplit[2].isEmpty()) {
                    optionalParameterConstraint = insideSplit[2];
                }

                if (!PARAM_NAME_PATTERN.matcher(parameterName).matches()) {
                    throw new IllegalArgumentException("Invalid parameter name '" + parameterName + "' in template '" + template + "'. " +
                        "The value must match '" + PARAM_NAME_PATTERN + "'");
                }

                segments.add(new ParameterTemplateSegment(parameterName, optionalParameterDelimiter, optionalParameterConstraint));
                i = end; // '}' is incremented by the loop

            } else {
                literal.append(c);
            }
        }

        if (!literal.isEmpty()) {
            segments.add(new LiteralTemplateSegment(literal.toString()));
        }

        return segments;
    }
}
