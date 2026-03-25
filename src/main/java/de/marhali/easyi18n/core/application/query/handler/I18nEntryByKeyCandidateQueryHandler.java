package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.cqrs.SynchronousQueryHandler;
import de.marhali.easyi18n.core.application.query.I18nEntryByKeyCandidateQuery;
import de.marhali.easyi18n.core.application.service.I18nKeyCandidateResolver;
import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.model.I18nEntry;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query handler for {@link I18nEntryByKeyCandidateQuery}.
 *
 * @author marhali
 */
public class I18nEntryByKeyCandidateQueryHandler implements SynchronousQueryHandler<I18nEntryByKeyCandidateQuery, Optional<I18nEntry>> {

    private final @NotNull I18nStore store;
    private final @NotNull I18nKeyCandidateResolver keyResolver;

    public I18nEntryByKeyCandidateQueryHandler(@NotNull I18nStore store, @NotNull I18nKeyCandidateResolver keyResolver) {
        this.store = store;
        this.keyResolver = keyResolver;
    }


    @Override
    public @NotNull PossiblyUnavailable<Optional<I18nEntry>> handle(@NotNull I18nEntryByKeyCandidateQuery query) {
        ModuleId moduleId = query.moduleId();
        I18nKeyCandidate keyCandidate = query.keyCandidate();

        if (!store.getSnapshot().hasModule(moduleId)) {
            return PossiblyUnavailable.unavailable();
        }

        I18nEntry entry = keyResolver.resolveExact(moduleId, keyCandidate);

        return PossiblyUnavailable.available(Optional.ofNullable(entry));
    }
}
