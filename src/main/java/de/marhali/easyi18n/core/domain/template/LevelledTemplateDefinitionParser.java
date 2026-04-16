package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for parsing hierarchical template strings.
 *
 * @author marhali
 */
public final class LevelledTemplateDefinitionParser {

    /**
     * Regular expression to find template level definition syntax strings.
     * <pre>
     * {@code
     * [myLevelA].[myLevelB] -> myLevelA, myLevelB
     * }
     * </pre>
     */
    private static final @NotNull Pattern LEVEL_PATTERN = Pattern.compile("\\[([^]]*)]");

    private LevelledTemplateDefinitionParser() {}

    public static @NotNull LevelledTemplate parse(@NotNull String templateDefinition) {

        if (!templateDefinition.startsWith("[") || !templateDefinition.endsWith("]")) {
            throw new IllegalArgumentException("Invalid levelled template definition: \"" + templateDefinition + "\". At least one level needs to be defined.");
        }

        List<Template> levels = new ArrayList<>();
        List<String> delimiters = new ArrayList<>();

        Matcher matcher = LEVEL_PATTERN.matcher(templateDefinition);

        int lastEnd = 0;
        boolean first = true;

        while (matcher.find()) {
            // Delimiter between two template levels
            if (!first) {
                var delimiterBetweenLevels = templateDefinition.substring(lastEnd, matcher.start());
                delimiters.add(delimiterBetweenLevels);
            }

            // Template definition syntax at level
            var templateDefinitionAtLevel = matcher.group(1);
            var templateAtLevel = TemplateDefinitionParser.parse(templateDefinitionAtLevel);
            levels.add(templateAtLevel);

            lastEnd = matcher.end();
            first = false;
        }

        return new LevelledTemplate(levels, delimiters);
    }
}
