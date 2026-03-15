package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.GuessNullableI18nEntryQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleTemplates;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Template;
import de.marhali.easyi18n.core.domain.template.TemplateElement;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Query handler for {@link GuessNullableI18nEntryQuery}.
 *
 * @author marhali
 */
public class GuessNullableI18nEntryQueryHandler
    implements QueryHandler<GuessNullableI18nEntryQuery, NullableI18nEntry> {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public GuessNullableI18nEntryQueryHandler(
        @NotNull CachedModuleTemplates cachedModuleTemplates,
        @NotNull ProjectConfigPort projectConfigPort
    ) {
        this.cachedModuleTemplates = cachedModuleTemplates;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull NullableI18nEntry handle(@NotNull GuessNullableI18nEntryQuery query) {
        LocaleId previewLocaleId = projectConfigPort.read().previewLocale();

        Templates templates = cachedModuleTemplates.resolve(query.moduleId());
        Template keyTemplate = templates.key().getTemplate();

        String input = query.input();
        boolean isLikeI18nKey = false;

        for (TemplateElement element : keyTemplate.elements()) {
            switch (element) {
                case TemplateElement.Literal literal -> {
                    if (input.contains(literal.text())) {
                        isLikeI18nKey = true;
                    }
                }
                case TemplateElement.Placeholder placeholder -> {
                    if (placeholder.delimiter() != null) {
                        int indexOfDelimiter = input.indexOf(placeholder.delimiter());
                        if (indexOfDelimiter != -1 && indexOfDelimiter != input.length() - 1) {
                            isLikeI18nKey = true;
                        }
                    }
                }
            }
        }

        return isLikeI18nKey
            ? new NullableI18nEntry(I18nKey.of(input), null)
            : new NullableI18nEntry(null,
                new I18nContent(Map.of(previewLocaleId, I18nValue.fromQuotedPrimitive(input)), null));
    }
}
