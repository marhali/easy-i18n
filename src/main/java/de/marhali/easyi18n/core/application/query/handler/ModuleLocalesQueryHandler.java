package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.ModuleLocalesQuery;
import de.marhali.easyi18n.core.application.service.EnsureLoadedService;
import de.marhali.easyi18n.core.application.service.LocalesOrderService;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Query handler for {@link ModuleLocalesQuery}.
 *
 * @author marhali
 */
public class ModuleLocalesQueryHandler implements QueryHandler<ModuleLocalesQuery, @NotNull Set<LocaleId>> {

    private final @NotNull LocalesOrderService localesOrderService;
    private final @NotNull EnsureLoadedService ensureLoadedService;
    private final @NotNull I18nStore store;

    public ModuleLocalesQueryHandler(@NotNull LocalesOrderService localesOrderService, @NotNull EnsureLoadedService ensureLoadedService, @NotNull I18nStore store) {
        this.localesOrderService = localesOrderService;
        this.ensureLoadedService = ensureLoadedService;
        this.store = store;
    }

    @Override
    public @NotNull Set<LocaleId> handle(@NotNull ModuleLocalesQuery query) {
        ensureLoadedService.ensureLoaded(query.moduleId());
        var moduleLocales = store.getSnapshot().getModuleOrThrow(query.moduleId()).locales();
        return localesOrderService.orderByPreviewLocale(moduleLocales);
    }
}
