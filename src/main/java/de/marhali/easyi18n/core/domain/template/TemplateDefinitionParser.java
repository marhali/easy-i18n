package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Parser for parsing {@link TemplateElement template} strings.
 *
 * @author marhali
 */
public final class TemplateDefinitionParser {

    /**
     * Regex pattern to match parameter names. Must be compliant to be used for regex named group.
     */
    private static final @NotNull Pattern PARAM_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");

    private TemplateDefinitionParser() {}

    /**
     * Parses the provided template definition into template elements.
     *
     * @param templateDefinition The template syntax string
     * @return List of parsed elements left-to-right
     */
    public static @NotNull Template parse(@NotNull String templateDefinition) {
        List<TemplateElement> segments = new ArrayList<>();
        StringBuilder literal = new StringBuilder();

        for (int i = 0; i < templateDefinition.length(); i++) {
            char c = templateDefinition.charAt(i);

            if (c == '{') {
                if (i > 0 && templateDefinition.charAt(i - 1) == '\\') {
                    // \{ -> Escaped parameter segment, we will ignore it from parsing
                    literal.deleteCharAt(literal.length() - 1);
                    literal.append(c);
                    continue;
                }

                if (!literal.isEmpty()) {
                    // Flush any existing literal before continuing with a parameter segment
                    segments.add(new TemplateElement.Literal(literal.toString()));
                    literal.setLength(0);
                }

                int end = templateDefinition.indexOf('}', i + 1);

                if (end == -1) {
                    throw new IllegalArgumentException("Missing closing parameter segment ('}') in template '" + templateDefinition + "'");
                }

                String inside = templateDefinition.substring(i + 1, end);

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
                    throw new IllegalArgumentException("Invalid parameter name '" + parameterName + "' in template '" + templateDefinition + "'. " +
                        "The value must match '" + PARAM_NAME_PATTERN + "'");
                }

                segments.add(new TemplateElement.Placeholder(parameterName, optionalParameterDelimiter, optionalParameterConstraint));
                i = end; // '}' is incremented by the loop

            } else {
                literal.append(c);
            }
        }

        if (!literal.isEmpty()) {
            segments.add(new TemplateElement.Literal(literal.toString()));
        }

        return new Template(templateDefinition, segments);
    }
}
