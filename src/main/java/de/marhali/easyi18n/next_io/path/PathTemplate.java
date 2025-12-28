package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nParamsBuilder;
import de.marhali.easyi18n.next_io.I18nPath;
import de.marhali.easyi18n.next_io.template.TemplateParser;
import de.marhali.easyi18n.next_io.template.TemplatePattern;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bidirectional path template.
 * Used for:
 * <ul>
 *     <li>matching appropriate paths according to the defined template syntax</li>
 *     <li>building paths according to the defined template syntax with filled parameters</li>
 * </ul>
 *
 * @author marhali
 */
public class PathTemplate {

    /**
     * By default, a path parameter can be anything expect:
     * <ul>
     *     <li>'/' (folder indicator)</li>
     *     <li>'.' (delimiter between file name and type)</li>
     * </ul>
     */
    private static final @NotNull String DEFAULT_PATH_CONSTRAINT = "[^/.]+";
    private final @NotNull String template;
    private final @NotNull List<TemplateSegment> segments;
    private final @NotNull Pattern pattern;
    private final @NotNull PathBuilder builder;
    private PathTemplate(
        @NotNull String template,
        @NotNull List<TemplateSegment> segments,
        @NotNull Pattern pattern
    ) {
        this.template = template;
        this.segments = segments;
        this.pattern = pattern;
        this.builder = new PathBuilder(segments);
    }

    /**
     * Compiles the provided template syntax string into a bidirectional path template.
     *
     * @param template Template syntax string
     * @return {@link PathTemplate}
     */
    public static @NotNull PathTemplate compile(@NotNull String template) {
        var segments = TemplateParser.parseSegments(template);
        var pattern = TemplatePattern.fromSegments(segments, DEFAULT_PATH_CONSTRAINT);

        return new PathTemplate(template, segments, pattern);
    }

    /**
     * Matches the provided path against the underlying template pattern.
     *
     * @param path Path to match against
     * @return {@code null} if path does not match, otherwise a {@link Map} with resolved parameter values.
     */
    public @Nullable I18nParams match(@NotNull String path) {
        Matcher matcher = pattern.matcher(path);

        if (!matcher.matches()) {
            return null;
        }

        I18nParamsBuilder paramsBuilder = I18nParams.builder();

        for (TemplateSegment segment : segments) {
            if (segment.isParameter()) {
                var parameter = segment.getAsParameter();
                var parameterName = parameter.getName();
                paramsBuilder.add(parameterName, parameter.splitByDelimiter(matcher.group(parameterName)));
            }
        }

        return paramsBuilder.build();
    }

    public @NotNull Set<I18nPath> build(@NotNull I18nParams params) {
        return builder.build(params);
    }

    /**
     * Retrieves the specified file extension.
     *
     * @return file extension as {@link String} or {@code null}, if a file extension could not be extracted
     */
    public @Nullable String getFileExtension() {
        int index = template.lastIndexOf(".");

        if (index == -1 || index + 1 >= template.length()) {
            return null;
        }

        return template.substring(index + 1);
    }

    @Override
    public String toString() {
        return "PathTemplate{" +
            "template='" + template + '\'' +
            ", segments=" + segments +
            ", pattern=" + pattern +
            ", builder=" + builder +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathTemplate that = (PathTemplate) o;
        return Objects.equals(template, that.template) && Objects.equals(segments, that.segments) && Objects.equals(pattern, that.pattern) && Objects.equals(builder, that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, segments, pattern, builder);
    }
}
