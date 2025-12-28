package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Template syntax regex transformer.
 *
 * @author marhali
 */
public class TemplatePattern {

    /**
     * Compiles the provided template syntax segments into a regex pattern.
     *
     * @param segments List of template syntax segments that should be transformed into a regex pattern.
     * @param defaultParameterConstraint Regex constraint to apply if a {@link ParameterTemplateSegment} does not specify one
     * @return {@link Pattern}
     */
    public static @NotNull Pattern fromSegments(@NotNull List<TemplateSegment> segments, @NotNull String defaultParameterConstraint) {
        StringBuilder regex = new StringBuilder();

        regex.append("^");

        for (TemplateSegment segment : segments) {
            if (segment.isLiteral()) {
                regex.append(Pattern.quote(segment.getAsLiteral().getText()));
            } else if (segment.isParameter()) {
                var parameter = segment.getAsParameter();
                var name = parameter.getName();
                var constraint = parameter.hasConstraint() ? parameter.getConstraint() : defaultParameterConstraint;

                // Named group: (?<name>pattern)
                regex.append("(?<").append(name).append(">").append(constraint).append(")");
            } else {
                throw new UnsupportedOperationException("Unknown template segment: " + segment.getClass().getSimpleName());
            }
        }

        regex.append("$");

        return Pattern.compile(regex.toString());
    }
}
