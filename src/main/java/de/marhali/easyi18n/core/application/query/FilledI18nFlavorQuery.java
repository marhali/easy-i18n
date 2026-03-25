package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @param moduleId Module identifier
 * @param key Translation key
 */
public record FilledI18nFlavorQuery(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key
    ) implements Query<String> {
}
