package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.next_io.template.TemplateLevelParser;
import de.marhali.easyi18n.next_io.template.TemplatePattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author marhali
 */
public class FileTemplate {

    private static final @NotNull String DEFAULT_FILE_CONSTRAINT = ".+";

    public static FileTemplate compile(@NotNull String template) {
        var levels = TemplateLevelParser.parseLevels(template);
        System.out.println(levels);
        var fileLevels = levels.stream()
            .map((level) -> new FileTemplateLevel(level, TemplatePattern.fromSegments(level.segments(), DEFAULT_FILE_CONSTRAINT)))
            .toList();
        return new FileTemplate(template, fileLevels);
    }

    private final @NotNull String template;
    private final @NotNull List<@NotNull FileTemplateLevel> levels;

    private FileTemplate(@NotNull String template, @NotNull List<@NotNull FileTemplateLevel> levels) {
        this.template = template;
        this.levels = levels;
    }

    public @NotNull FileTemplateLevel getAtLevel(@NotNull Integer level) {
        if (level < 0) {
            throw new IllegalArgumentException("The file level cannot be less than zero");
        }

        if (level >= levels.size()) {
            // We assume that the last segment always functions as a wildcard section with unlimited count
            return levels.getLast();
        }

        return levels.get(level);
    }

    public @NotNull List<@NotNull FileTemplateLevel> getLevels() {
        return this.levels;
    }

    // TODO: do we still need to add build() method here? should be already contained inside the specific level
}
