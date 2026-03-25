package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.model.I18nParamsBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Template value resolver using regular expressions.
 *
 * @author marhali
 */
public class RegExpTemplateValueResolver implements TemplateValueResolver {

    /**
     * Compiles the provided template into a regex pattern.
     *
     * @param template Template definition
     * @param defaultPlaceholderConstraint Regex constraint to apply if a {@link TemplateElement.Placeholder} does not specify one
     * @return {@link Pattern}
     */
    public static @NotNull RegExpTemplateValueResolver fromTemplate(@NotNull Template template, @NotNull String defaultPlaceholderConstraint) {
        StringBuilder regex = new StringBuilder();

        regex.append("^");

        for (TemplateElement element : template.elements()) {
            if (element.isLiteral()) {
                regex.append(Pattern.quote(element.getAsLiteral().text()));
            } else if (element.isPlaceholder()) {
                var placeholder = element.getAsPlaceholder();
                String placeholderName = placeholder.name();
                var constraint = placeholder.hasConstraint() ? placeholder.constraint() : defaultPlaceholderConstraint;

                // Named group: (?<name>pattern)
                regex.append("(?<").append(placeholderName).append(">").append(constraint).append(")");
            } else {
                throw new UnsupportedOperationException("Unknown template element: " + element.getClass().getSimpleName());
            }
        }

        regex.append("$");

        Pattern pattern = Pattern.compile(regex.toString());

        return new RegExpTemplateValueResolver(template, pattern);
    }

    private final @NotNull Template template;
    private final @NotNull Pattern pattern;

    private RegExpTemplateValueResolver(@NotNull Template template, @NotNull Pattern pattern) {
        this.template = template;
        this.pattern = pattern;
    }

    @Override
    public @Nullable I18nParams resolve(@NotNull String input) {
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return null;
        }

        I18nParamsBuilder paramsBuilder = I18nParams.builder();

        for (TemplateElement element : template.elements()) {
            if (element.isPlaceholder()) {
                TemplateElement.Placeholder placeholder = element.getAsPlaceholder();
                String placeholderName = placeholder.name();
                List<@NotNull String> placeholderValues = placeholder.splitByDelimiter(matcher.group(placeholderName));
                paramsBuilder.add(placeholderName, placeholderValues);
            }
        }

        return paramsBuilder.build();
    }
}
