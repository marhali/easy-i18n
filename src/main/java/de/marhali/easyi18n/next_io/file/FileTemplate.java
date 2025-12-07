package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.next_io.template.ParameterTemplateSegment;
import de.marhali.easyi18n.next_io.template.TemplateParser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * @author marhali
 */
public class FileTemplate {

    /**
     * Default count to use if a segment does not specify how many sections it covers.
     */
    private static final int DEFAULT_SEGMENT_COUNT = 1;

    public static FileTemplate compile(@NotNull String template) {
        var segments = TemplateParser.parseSegments(template);

        var parameterSegments = segments.stream()
            .map(segment -> {
                if (!segment.isParameter()) {
                    throw new IllegalArgumentException("The file template may only consist of parameter segments");
                }
                return segment.getAsParameter();
            })
            .toList();

        return new FileTemplate(template, parameterSegments);
    }

    private final @NotNull String template;
    private final @NotNull List<ParameterTemplateSegment> segments;

    private FileTemplate(@NotNull String template, @NotNull List<ParameterTemplateSegment> segments) {
        this.template = template;
        this.segments = segments;
    }

    public @NotNull ParameterTemplateSegment getSegmentAtDepth(int depth) {
        int i = 0;
        for (ParameterTemplateSegment segment : segments) {
            var count = getSegmentCount(segment);

            if (depth < i + count) {
                return segment;
            }

            i += count;
        }

        // We assume that the last segment always functions as a wildcard section with unlimited count
        return segments.getLast().getAsParameter();
    }

    private int getSegmentCount(@NotNull ParameterTemplateSegment segment) {
        if (segment.getConstraint() == null) {
            return DEFAULT_SEGMENT_COUNT;
        }

        try {
            return Integer.parseInt(segment.getConstraint());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Could not parse integer count for template parameter with name '" + segment.getName() + "'");
        }
    }
}
