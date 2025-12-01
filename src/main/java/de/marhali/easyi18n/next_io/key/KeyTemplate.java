package de.marhali.easyi18n.next_io.key;

import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_io.template.TemplateParser;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author marhali
 */
public class KeyTemplate {

    public static KeyTemplate compile(@NotNull String template) {
        var segments = TemplateParser.parseSegments(template);

        return new KeyTemplate(template, segments);
    }

    private final @NotNull String template;
    private final @NotNull List<TemplateSegment> segments;

    private KeyTemplate(@NotNull String template, @NotNull List<TemplateSegment> segments) {
        this.template = template;
        this.segments = segments;
    }

    // TODO: add stringify and parse methods

    public @NotNull I18nKey build(@NotNull Map<String, List<String>> params) {
        List<String> parts = new ArrayList<>();

        for (TemplateSegment segment : segments) {
            if (segment.isParameter()) {
                var parameter = segment.getAsParameter();
                parts.addAll(params.getOrDefault(parameter.getName(), List.of()));
            }
        }

        return I18nKey.of(parts);
    }
}
