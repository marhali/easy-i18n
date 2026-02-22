package de.marhali.easyi18n.core.domain.template;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a parsed template.
 *
 * <pre>
 * {@code
 * // Template definition syntax
 * hardCodedLiteral.{placeholderName:optionalDelimiter:optionalConstraint}
 *
 * // Example
 * $PROJECT_DIR$/locales/{locale::[^.]+}.json
 * }
 * </pre>
 *
 * @param canonical Template syntax
 * @param elements Parsed template elements
 */
public record Template(
    @NotNull String canonical,
    @NotNull List<@NotNull TemplateElement> elements
    ) {
}
