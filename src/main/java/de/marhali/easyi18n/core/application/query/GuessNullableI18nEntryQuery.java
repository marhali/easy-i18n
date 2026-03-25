package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.model.NullableI18nEntry;
import org.jetbrains.annotations.NotNull;

/**
 * Query to guess the {@link NullableI18nEntry} from an input string.
 * Basically this will check if the input string is likely a translation key,
 * otherwise it will be guessed as translation value for the preview locale.
 *
 * @param moduleId Module identifier
 * @param input Input string
 *
 * @author marhali
 */
public record GuessNullableI18nEntryQuery(
    @NotNull ModuleId moduleId,
    @NotNull String input
    ) implements Query<NullableI18nEntry> {
}
