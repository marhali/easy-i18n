package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.cqrs.SynchronousQueryHandler;
import de.marhali.easyi18n.core.application.query.I18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.service.I18nKeyCandidateResolver;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link I18nEntryPreviewQuery}.
 *
 * @author marhali
 */
public class I18nEntryPreviewQueryHandler implements SynchronousQueryHandler<I18nEntryPreviewQuery, Optional<I18nEntryPreview>> {

    private final @NotNull I18nStore store;
    private final @NotNull I18nKeyCandidateResolver keyResolver;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public I18nEntryPreviewQueryHandler(@NotNull I18nStore store, @NotNull I18nKeyCandidateResolver keyResolver, @NotNull ProjectConfigPort projectConfigPort) {
        this.store = store;
        this.keyResolver = keyResolver;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull PossiblyUnavailable<Optional<I18nEntryPreview>> handle(@NotNull I18nEntryPreviewQuery query) {
        ModuleId moduleId = query.moduleId();
        I18nKeyCandidate keyCandidate = query.keyCandidate();

        if (!store.getSnapshot().hasModule(moduleId)) {
            return PossiblyUnavailable.unavailable();
        }

        I18nEntry entry = keyResolver.resolveExact(moduleId, keyCandidate);
        LocaleId previewLocaleId = projectConfigPort.read().previewLocale();
        I18nEntryPreview entryPreview = entry != null ? I18nEntryPreview.fromEntry(entry, previewLocaleId) : null;

        return PossiblyUnavailable.available(Optional.ofNullable(entryPreview));
    }
}
