package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.cqrs.SynchronousQueryHandler;
import de.marhali.easyi18n.core.application.query.AllModuleI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Query handler for {@link AllModuleI18nEntryPreviewQuery}.
 *
 * @author marhali
 */
public class AllModuleI18nEntryPreviewQueryHandler implements SynchronousQueryHandler<AllModuleI18nEntryPreviewQuery, List<I18nEntryPreview>> {

    private final @NotNull I18nStore store;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public AllModuleI18nEntryPreviewQueryHandler(@NotNull I18nStore store, @NotNull ProjectConfigPort projectConfigPort) {
        this.store = store;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull PossiblyUnavailable<List<I18nEntryPreview>> handle(@NotNull AllModuleI18nEntryPreviewQuery query) {
        ModuleId moduleId = query.moduleId();

        if (!store.getSnapshot().hasModule(moduleId)) {
            return PossiblyUnavailable.unavailable();
        }

        ProjectConfig projectConfig = projectConfigPort.read();
        LocaleId previewLocaleId = projectConfig.previewLocale();
        Set<I18nKeyPrefix> defaultKeyPrefixes = projectConfig.modules().get(moduleId).defaultKeyPrefixes();

        I18nModule moduleStore = store.getSnapshot().getModuleOrThrow(moduleId);
        List<I18nEntryPreview> entries = new ArrayList<>();

        for (Map.Entry<@NotNull I18nKey, @NotNull I18nContent> entry : moduleStore.translations().entrySet()) {
            entries.add(I18nEntryPreview.fromEntry(I18nEntry.fromEntry(entry), previewLocaleId));

            for (I18nKeyPrefix defaultKeyPrefix : defaultKeyPrefixes) {
                if (defaultKeyPrefix.isPrefixed(entry.getKey())) {
                    entries.add(new I18nEntryPreview(
                        defaultKeyPrefix.withoutPrefix(entry.getKey()),
                        entry.getValue().values().get(previewLocaleId)
                    ));
                }
            }
        }

        return PossiblyUnavailable.available(entries);
    }
}
