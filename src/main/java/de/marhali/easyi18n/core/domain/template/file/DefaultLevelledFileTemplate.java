package de.marhali.easyi18n.core.domain.template.file;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.template.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Standard implementation for the leveled file template.
 *
 * @author marhali
 */
public class DefaultLevelledFileTemplate implements LevelledFileTemplate {

    private final @NotNull Template template;
    private final @NotNull TemplateValueResolver resolver;
    private final @NotNull TemplateValueFormulator formulator;

    public DefaultLevelledFileTemplate(
        @NotNull Template template,
        @NotNull TemplateValueResolver resolver,
        @NotNull TemplateValueFormulator formulator
    ) {
        this.template = template;
        this.resolver = resolver;
        this.formulator = formulator;
    }

    @Override
    public @NotNull I18nParams fromCanonical(@NotNull String canonical) {
        I18nParams params = resolver.resolve(canonical);

        // Only check if resolver matches. Actual params might be empty if template contains only literal elements
        if (params == null) {
            throw new IllegalArgumentException("Cannot parse canonical file level \"" + canonical + "\" against template \"" + template.canonical() + "\"");
        }

        return params;
    }

    @Override
    public @NotNull Set<@NotNull TemplateValue> buildVariants(@NotNull I18nParams params) {
        return formulator.buildVariants(params);
    }

    @Override
    public @NotNull Set<TemplateElement.@NotNull Placeholder> getNeededParameters() {
        return template.elements().stream()
            .filter(TemplateElement::isPlaceholder)
            .map(TemplateElement::getAsPlaceholder)
            .collect(Collectors.toSet());
    }
}
