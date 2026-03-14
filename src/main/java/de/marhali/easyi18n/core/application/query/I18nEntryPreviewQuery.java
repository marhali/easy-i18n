package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.SynchronousQuery;
import de.marhali.easyi18n.core.domain.model.I18nEntryPreview;
import de.marhali.easyi18n.core.domain.model.I18nKeyCandidate;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 *
 * @param moduleId Module identifier
 * @param keyCandidate Translation key candidate
 *
 * @author marhali
 */
public record I18nEntryPreviewQuery(
    @NotNull ModuleId moduleId,
    @NotNull I18nKeyCandidate keyCandidate
) implements SynchronousQuery<Optional<I18nEntryPreview>> {
}
