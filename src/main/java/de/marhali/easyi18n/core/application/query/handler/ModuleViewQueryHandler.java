package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.ModuleViewQuery;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.ModuleViewProjector;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Module view query handler using the underlying {@link ModuleViewProjector}.
 *
 * @author marhali
 */
public class ModuleViewQueryHandler implements QueryHandler<ModuleViewQuery, ModuleView> {

    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull I18nStore store;
    private final @NotNull ModuleViewProjector moduleViewProjector;

    public ModuleViewQueryHandler(@NotNull EnsureLoadedService ensureLoadedService, @NotNull I18nStore store, @NotNull ModuleViewProjector moduleViewProjector) {
        this.ensureLoadedService = ensureLoadedService;
        this.store = store;
        this.moduleViewProjector = moduleViewProjector;
    }

    @Override
    public @NotNull ModuleView handle(@NotNull ModuleViewQuery query) {
        ensureLoadedService.ensureLoaded(query.moduleId());
        I18nModule module = store.getSnapshot().getOrEmptyModule(query.moduleId());
        return moduleViewProjector.project(query.moduleId(), module, query.options());
    }
}
