package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.FilledI18nFlavorQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleTemplates;
import de.marhali.easyi18n.core.domain.template.Templates;
import org.jetbrains.annotations.NotNull;

/**
 * Query handler for {@link FilledI18nFlavorQuery}.
 *
 * @author marhali
 */
public class FilledI18nFlavorQueryHandler implements QueryHandler<FilledI18nFlavorQuery, String> {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;

    public FilledI18nFlavorQueryHandler(@NotNull CachedModuleTemplates cachedModuleTemplates) {
        this.cachedModuleTemplates = cachedModuleTemplates;
    }

    @Override
    public @NotNull String handle(@NotNull FilledI18nFlavorQuery query) {
        Templates templates = cachedModuleTemplates.resolve(query.moduleId());
        return templates.flavor().fromI18nKey(query.key());
    }
}
