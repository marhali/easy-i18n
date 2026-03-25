package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.TranslationByKeyQuery;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.I18nContent;
import de.marhali.easyi18n.core.domain.model.I18nModule;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link TranslationByKeyQuery}.
 *
 * @author marhali
 */
public class TranslationByKeyQueryHandler implements QueryHandler<TranslationByKeyQuery, Optional<I18nContent>> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull I18nStore store;

    public TranslationByKeyQueryHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull I18nStore store) {
        this.ensureLoadedService = ensureLoadedService;
        this.store = store;
    }

    @Override
    public @NotNull Optional<I18nContent> handle(@NotNull TranslationByKeyQuery query) {
        ensureLoadedService.ensureLoaded(query.moduleId());
        I18nModule module = store.getSnapshot().getModuleOrThrow(query.moduleId());
        return Optional.ofNullable(module.getTranslation(query.key()));
    }
}
