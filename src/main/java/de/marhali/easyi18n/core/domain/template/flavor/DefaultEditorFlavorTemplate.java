package de.marhali.easyi18n.core.domain.template.flavor;

import de.marhali.easyi18n.core.domain.model.I18nBuiltinParam;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.template.Template;
import de.marhali.easyi18n.core.domain.template.TemplateDefinitionParser;
import de.marhali.easyi18n.core.domain.template.TemplateValueFormulator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default implementation for {@link EditorFlavorTemplate}.
 *
 * @author marhali
 */
public class DefaultEditorFlavorTemplate implements EditorFlavorTemplate {

    public static @NotNull DefaultEditorFlavorTemplate compile(@NotNull String templateDefinition) {
        var template = TemplateDefinitionParser.parse(templateDefinition);
        var formulator = new TemplateValueFormulator(template);
        return new DefaultEditorFlavorTemplate(template, formulator);
    }

    private final @NotNull Template template;
    private final @NotNull TemplateValueFormulator formulator;

    public DefaultEditorFlavorTemplate(@NotNull Template template, @NotNull TemplateValueFormulator formulator) {
        this.template = template;
        this.formulator = formulator;
    }

    @Override
    public @NotNull String fromI18nKey(@NotNull I18nKey key) {
        return formulator.buildVariants(
            I18nParams.builder()
                .put(I18nBuiltinParam.I18N_KEY, List.of(key.canonical()))
                .build())
            .stream()
            .findFirst().orElseThrow(() -> new IllegalArgumentException(
                "Could not build exactly one flavor for template \"" + template.canonical() + "\" with key \"" + key.canonical() + "\""
            ))
            .value();
    }
}
