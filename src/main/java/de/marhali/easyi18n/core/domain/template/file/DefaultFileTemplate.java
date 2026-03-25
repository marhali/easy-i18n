  package de.marhali.easyi18n.core.domain.template.file;

import de.marhali.easyi18n.core.domain.template.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Standard implementation for the file template.
 *
 * @author marhali
 */
public class DefaultFileTemplate implements FileTemplate {
    /**
     * Shorthand to construct the file template by the template definition.* @param fileTemplateDefinition Template definition
     * @return {@link DefaultFileTemplate}
     */
    public static @NotNull DefaultFileTemplate compile(@NotNull String fileTemplateDefinition) {
        var template = LevelledTemplateDefinitionParser.parse(fileTemplateDefinition);

        List<LevelledFileTemplate> levels = template.levels().stream()
            .map((level) -> new DefaultLevelledFileTemplate(
                level,
                RegExpTemplateValueResolver.fromTemplate(level, DEFAULT_FILE_CONSTRAINT),
                new TemplateValueFormulator(level)
        )).collect(Collectors.toUnmodifiableList());

        return new DefaultFileTemplate(template, levels);
    }

    /**
     * By default, a file parameter can be anything expect:
     * <ul>
     *     <li>'.' (delimiter between file name and type)</li>
     * </ul>
     */
    private static final @NotNull String DEFAULT_FILE_CONSTRAINT = ".+";

    private final @NotNull LevelledTemplate template;
    public final @NotNull List<@NotNull LevelledFileTemplate> levels;

    public DefaultFileTemplate(@NotNull LevelledTemplate template, @NotNull List<@NotNull LevelledFileTemplate> levels) {
        this.template = template;
        this.levels = levels;
    }

    @Override
    public @NotNull LevelledFileTemplate getAtLevel(@NotNull Integer level) {
        if (level < 0) {
            throw new IllegalArgumentException("Level cannot be negative");
        }

        if (level >= levels.size()) {
            // We assume that the last segment always functions as a wildcard section with unlimited count
            return levels.getLast();
        }

        return levels.get(level);
    }

    @Override
    public @NotNull List<LevelledFileTemplate> getLevels() {
        return levels;
    }

    @Override
    public boolean needsParameter(@NotNull String parameterName) {
        for (LevelledFileTemplate level : levels) {
            for (TemplateElement.Placeholder neededParameter : level.getNeededParameters()) {
                if (neededParameter.name().equals(parameterName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
