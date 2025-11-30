package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_io.template.TemplateParser;
import de.marhali.easyi18n.next_io.template.TemplatePattern;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private static final String DEFAULT_PATH_CONSTRAINT = "[^/.]+";

    /**
     * Compiles the provided template syntax string into a bidirectional path template.
     * @param template Template syntax string
     * @return {@link PathTemplate}
     */
    public static PathTemplate compile(String template) {
        Objects.requireNonNull(template, "template must not be null");

        var segments = TemplateParser.parseSegments(template);
        var pattern = TemplatePattern.fromSegments(segments, DEFAULT_PATH_CONSTRAINT);

        return new PathTemplate(template, segments, pattern);
    }

    private final String template;
    private final List<TemplateSegment> segments;
    private final Pattern pattern;

    private PathTemplate(String template, List<TemplateSegment> segments, Pattern pattern) {
        this.template = template;
        this.segments = segments;
        this.pattern = pattern;
    }

    /**
     * Matches the provided path against the underlying template pattern.
     * @param path Path to match against
     * @return {@code null} if path does not match, otherwise a {@link Map} with resolved parameter values.
     */
    public @Nullable Map<String, String> match(String path) {
        Objects.requireNonNull(path, "path must not be null");

        Matcher matcher = pattern.matcher(path);

        if (!matcher.matches()) {
            return null;
        }

        Map<String, String> params = new HashMap<>();

        for (TemplateSegment segment : segments) {
            if (segment.isParameter()) {
                var name = segment.getAsParameter().getName();
                params.put(name, matcher.group(name));
            }
        }

        return params;
    }

    /**
     * Builds the path with the provided parameters and the underlying template syntax.
     * @param params Parameters to use to build the path
     * @return Build path
     */
    public String build(Map<String, ?> params) {
        Objects.requireNonNull(params, "params must not be null");

        StringBuilder sb = new StringBuilder();

        for (TemplateSegment segment : segments) {
           if (segment.isLiteral()) {
               sb.append(segment.getAsLiteral().getLiteral());
           } else if (segment.isParameter()) {
               var name = segment.getAsParameter().getName();
               var value = params.get(name);

               if (value == null) {
                   throw new NullPointerException("Parameter by key '" + name + "' must not be null");
               }

               // TODO: we might also validate the value against the parameter constraint

               sb.append(value);
           }
        }

        return sb.toString();
    }

    /**
     * Retrieves the specified file extension.
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
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathTemplate that = (PathTemplate) o;
        return Objects.equals(template, that.template) && Objects.equals(segments, that.segments) && Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, segments, pattern);
    }
}
