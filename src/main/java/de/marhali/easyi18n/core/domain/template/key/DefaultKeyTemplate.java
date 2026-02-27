package de.marhali.easyi18n.core.domain.template.key;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard implementation for the key template.
 *
 * @author marhali
 */
public class DefaultKeyTemplate implements KeyTemplate {

    /**
     * Shorthand to construct the key template by the template definition
     * @param keyTemplateDefinition Template definition
     * @return {@link DefaultKeyTemplate}
     */
    public static @NotNull DefaultKeyTemplate compile(@NotNull String keyTemplateDefinition) {
        var template = TemplateDefinitionParser.parse(keyTemplateDefinition);
        var resolve = RegExpTemplateValueResolver.fromTemplate(template, DEFAULT_KEY_CONSTRAINT);
        return new DefaultKeyTemplate(template, resolve);
    }

    /**
     * By default, a key parameter can be anything.
     */
    private static final @NotNull String DEFAULT_KEY_CONSTRAINT = ".+";

    private final @NotNull Template template;
    private final @NotNull TemplateValueResolver resolver;

    public DefaultKeyTemplate(@NotNull Template template, @NotNull TemplateValueResolver resolver) {
        this.template = template;
        this.resolver = resolver;
    }

    @Override
    public @NotNull I18nParams toParams(@NotNull I18nKey key) {
        I18nParams params = resolver.resolve(key.canonical());

        // Other than file or path template, we can guarantee that a translation key has parameters
        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Cannot parse key against key template: " + key);
        }

        return params;
    }

    @Override
    public @NotNull I18nKey fromParams(@NotNull I18nParams params) {
        StringBuilder builder = new StringBuilder();

        for (TemplateElement element : template.elements()) {
            switch (element) {
                case TemplateElement.Literal literal -> builder.append(literal.text());
                case TemplateElement.Placeholder placeholder -> {
                    var values = params.get(placeholder.name());

                    if (values == null || values.isEmpty()) {
                        throw new IllegalArgumentException("Missing parameters for placeholder with name: " + placeholder.name());
                    }

                    builder.append(placeholder.joinByDelimiter(values));
                }
                default -> throw new IllegalArgumentException("Unknown template element: " + element.getClass().getSimpleName());
            }
        }

        return new I18nKey(builder.toString());
    }

    @Override
    public @NotNull List<@NotNull String> toHierarchy(@NotNull I18nKey key) {
        var params = toParams(key);
        var levels = new ArrayList<String>();

        for (TemplateElement element : template.elements()) {
            switch (element) {
                case TemplateElement.Literal literal -> levels.add(literal.text());
                case TemplateElement.Placeholder placeholder -> {
                    var values = params.get(placeholder.name());

                    if (values == null) {
                        throw new IllegalStateException("Missing values for placeholder with name: " + placeholder.name());
                    }

                    levels.addAll(values);
                }
            }
        }

        return levels;
    }
}
