package de.marhali.easyi18n.core.domain.template.path;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.template.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard implementation for {@link PathTemplate}.
 *
 * @author marhali
 */
public class DefaultPathTemplate implements PathTemplate {

    /**
     * Shorthand to construct the path template by the template definition.
     * @param templateDefinition Template definition
     * @return {@link DefaultPathTemplate}
     */
    public static @NotNull DefaultPathTemplate compile(@NotNull String templateDefinition) {
        var template = TemplateDefinitionParser.parse(templateDefinition);
        var resolver = RegExpTemplateValueResolver.fromTemplate(template, DEFAULT_PATH_CONSTRAINT);
        var formulator = new TemplateValueFormulator(template);
        return new DefaultPathTemplate(template, resolver, formulator);
    }

    /**
     * By default, a path parameter can be anything expect:
     * <ul>
     *     <li>'/' (folder indicator)</li>
     *     <li>'.' (delimiter between file name and type)</li>
     * </ul>
     */
    private static final @NotNull String DEFAULT_PATH_CONSTRAINT = "[^/.]+";

    private final @NotNull Template template;
    private final @NotNull TemplateValueResolver resolver;
    private final @NotNull TemplateValueFormulator formulator;

    private final @NotNull String fileExtension;
    private final @NotNull String mostCommonParentPath;

    public DefaultPathTemplate(
        @NotNull Template template,
        @NotNull TemplateValueResolver resolver,
        @NotNull TemplateValueFormulator formulator
    ) {
        this.template = template;
        this.resolver = resolver;
        this.formulator = formulator;
        this.fileExtension = computeFileExtension(template);
        this.mostCommonParentPath = computeMostCommonParentPath(template);
    }

    @Override
    public @NotNull Template getTemplate() {
        return template;
    }

    @Override
    public @NotNull String toCanonical(@NotNull I18nPath path) {
        return path.canonical();
    }

    @Override
    public @NotNull I18nPath fromCanonical(@NotNull String canonical) {
        I18nParams params = resolver.resolve(canonical);

        // Only check if resolver matches. Actual params might be empty if template contains only literal elements
        if (params == null) {
            throw new IllegalArgumentException("Cannot parse canonical path \"" + canonical + "\" against path template \"" + canonical + "\"");
        }

        return new I18nPath(canonical, params);
    }

    @Override
    public @Nullable I18nParams matchCanonical(@NotNull String canonical) {
        return resolver.resolve(canonical);
    }

    @Override
    public @NotNull Set<@NotNull I18nPath> buildVariants(@NotNull I18nParams params) {
        return formulator.buildVariants(params).stream()
            .map(I18nPath::fromTemplateValue)
            .collect(Collectors.toSet());
    }

    @Override
    public @NotNull String getFileExtension() {
        return fileExtension;
    }

    @Override
    public @NotNull String getMostCommonParentPath() {
        return mostCommonParentPath;
    }

    private static @NotNull String computeFileExtension(@NotNull Template template) {
        int index = template.canonical().lastIndexOf(".");

        if (index == 0 || index == -1 || index + 1 >= template.canonical().length()) {
            throw new IllegalArgumentException("Could not extract file extension from path template \"" + template.canonical() + "\"");
        }

        return template.canonical().substring(index + 1);
    }

    private static @NotNull String computeMostCommonParentPath(@NotNull Template template) {
        StringBuilder builder = new StringBuilder();

        for (TemplateElement element : template.elements()) {
            if (element.isLiteral()) {
                builder.append(element.getAsLiteral().text());
            } else {
                // Break at first non-literal (placeholder) element
                break;
            }
        }

        String path = builder.toString();

        if (path.isEmpty()) {
            throw new IllegalArgumentException("Most common parent path for template is empty \"" + template + "\"");
        }

        // If the placeholder falls inside a filename rather than a directory segment
        // (e.g. "…/messages_{locale}.properties"), trim back to the last directory separator
        // so that the result always points to the containing directory.
        if (!path.endsWith("/")) {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash >= 0) {
                path = path.substring(0, lastSlash + 1);
            }
            // No slash at all: template has no directory structure — keep full literal prefix.
        }

        return path;
    }
}
