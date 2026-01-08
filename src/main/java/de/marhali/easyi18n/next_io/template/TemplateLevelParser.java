package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for multi-level template syntax strings.
 *
 * @author marhali
 */
public class TemplateLevelParser {

    public static @NotNull List<@NotNull TemplateLevel> parseLevels(@NotNull String template) {
        List<TemplateLevel> levels = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < template.length(); i++) {
            char c = template.charAt(i);

            if (escaping) {
                // Appending an escaped char
                builder.append(c);
                escaping = false;
            } else if (c == '\\') {
                // Indicate that the next char is escaped
                escaping = true;
            } else if (c == ';') {
                // Indicate a level (section) break
                var templateAtLevel = builder.toString();
                var segmentsAtLevel = TemplateParser.parseSegments(templateAtLevel);
                levels.add(new TemplateLevel(levels.size(), segmentsAtLevel));
                builder.setLength(0);
            } else {
                // Appending normal char at current level (section)
                builder.append(c);
            }
        }

        if (escaping) {
            // Incomplete escape sequence
            throw new IllegalArgumentException("Incomplete escape sequence in template: '" + template + "'");
        }

        if (!builder.isEmpty()) {
            levels.add(new TemplateLevel(levels.size(), TemplateParser.parseSegments(builder.toString())));
        }

        return levels;
    }
}
