package de.marhali.easyi18n.next_io.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private static final Pattern PARAM_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    /**
     * Parses the provided template string into template segments.
     *
     * @param template The template syntax string
     * @return List of parsed segments left-to-right
     */
    public static List<TemplateSegment> parseSegments(String template) {
        Objects.requireNonNull(template, "template must not be null");

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

                String[] insideSplit = inside.split(":", 2);

                String parameterName = insideSplit[0];
                String optionalParameterConstraint = null;

                if (insideSplit.length > 1) {
                    optionalParameterConstraint = insideSplit[1];
                }

                if (!PARAM_NAME_PATTERN.matcher(parameterName).matches()) {
                    throw new IllegalArgumentException("Invalid parameter name '" + parameterName + "' in template '" + template + "'. " +
                        "The value must match '" + PARAM_NAME_PATTERN + "'");
                }

                segments.add(new ParameterTemplateSegment(parameterName, optionalParameterConstraint));
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
