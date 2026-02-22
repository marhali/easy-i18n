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

        // TODO: Commented isEmpty() because literal (hard coded segments) might not require params
        if (params == null /*|| params.isEmpty()*/) {
            throw new IllegalArgumentException("Cannot parse canonical file level against template: " + template.canonical());
        }

        return params;
    }

    @Override
    public @NotNull Set<@NotNull TemplateValue> buildVariants(@NotNull I18nParams params) {
        return formulator.buildVariants(params);
    }

    @Override
    public Set<String> getNeededParameterNames() {
        return template.elements().stream()
            .filter(TemplateElement::isPlaceholder)
            .map((element) -> element.getAsPlaceholder().name())
            .collect(Collectors.toSet());
    }
}
