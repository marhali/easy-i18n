package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.SynchronousQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntry;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query to resolve a translation entry by a translation key candidate.
 *
 * @param moduleId Module identifier
 * @param keyCandidate Translation key candidate
 *
 * @author marhali
 */
public record I18nEntryByKeyCandidateQuery(
    @NotNull ModuleId moduleId,
    @NotNull I18nKeyCandidate keyCandidate
) implements SynchronousQuery<Optional<I18nEntry>> {
}
