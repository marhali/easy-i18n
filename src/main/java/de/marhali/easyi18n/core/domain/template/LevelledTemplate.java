package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Hierarchical parsed template with multiple levels and delimiters between each.
 *
 * <pre>
 * {@code
 * // Levelled template definition syntax
 * [template at level 0]delimiter A[template at level 1]
 *
 * // Example
 * [{locale}].[hardCodedLiteral].[{fileKey}]
 * }
 * </pre>
 *
 * @param levels Template levels
 * @param delimiters Delimiters between each level
 *
 * @author marhali
 */
public record LevelledTemplate(
    @NotNull List<@NotNull Template> levels,
    @NotNull List<@NotNull String> delimiters
    ) {
}
