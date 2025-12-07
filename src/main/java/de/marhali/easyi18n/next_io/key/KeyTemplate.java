package de.marhali.easyi18n.next_io.key;

import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_io.template.TemplateParser;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    // TODO: add stringify method and parse from single string method

    public @NotNull I18nParams parse(@NotNull I18nKey key) {
        I18nParams params = new I18nParams();

        var segmentsIterator = segments.iterator();
        var partsIterator = key.parts().iterator();

        TemplateSegment currentSegment = null;

        while (partsIterator.hasNext()) {
            var part = partsIterator.next();

            while (segmentsIterator.hasNext() && (currentSegment == null || !currentSegment.isParameter())) {
                currentSegment = segmentsIterator.next();
            }

            if (currentSegment == null || !currentSegment.isParameter()) {
                throw new NullPointerException("Cannot associate key part '" + part + "' with any parameter template segment");
            }

            var parameter = currentSegment.getAsParameter();
            var name = parameter.getName();

            params.add(name, part);

            if (partsIterator.hasNext() && segmentsIterator.hasNext()) {
                currentSegment = null;
            }
        }

        return params;
    }

    public @NotNull I18nKey build(@NotNull I18nParams params) {
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
