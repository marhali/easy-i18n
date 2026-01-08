package de.marhali.easyi18n.next_io.template;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @param level The template level
 * @param segments Segments that represent this level
 *
 * @author marhali
 */
public record TemplateLevel(
    @NotNull Integer level,
    @NotNull List<@NotNull TemplateSegment> segments
) {
}
