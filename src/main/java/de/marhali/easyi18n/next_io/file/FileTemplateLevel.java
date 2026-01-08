package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nParamsBuilder;
import de.marhali.easyi18n.next_io.I18nPath;
import de.marhali.easyi18n.next_io.path.PathBuilder;
import de.marhali.easyi18n.next_io.template.TemplateLevel;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @param level
 * @param pattern
 *
 * @author marhali
 */
public record FileTemplateLevel(
    @NotNull TemplateLevel level,
    @NotNull Pattern pattern
    ) {
    public @NotNull I18nParams parse(@NotNull String input) {
        Matcher matcher = pattern.matcher(input);

        I18nParamsBuilder paramsBuilder = I18nParams.builder();

        if (matcher.matches()) {
            for (TemplateSegment segment : level.segments()) {
                if (segment.isParameter()) {
                    var parameter = segment.getAsParameter();
                    var parameterName = parameter.getName();
                    paramsBuilder.add(parameterName, parameter.splitByDelimiter(matcher.group(parameterName)));
                }
            }
        }

        return paramsBuilder.build();
    }

    public @NotNull Set<String> build(@NotNull I18nParams params) {
        // TODO: do not use path builder here
        return new PathBuilder(level.segments()).build(params).stream()
            .map(I18nPath::path).collect(Collectors.toSet());
    }
}
