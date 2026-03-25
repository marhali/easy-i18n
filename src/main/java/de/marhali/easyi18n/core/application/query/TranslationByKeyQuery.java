package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.I18nContent;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Query to retrieve the {@link I18nContent content} for a specific translation by key.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 */
public record TranslationByKeyQuery(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key
    ) implements Query<Optional<I18nContent>> {
}
