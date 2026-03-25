package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to delete a localized value from an existing translation.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 * @param localeId Locale identifier
 *
 * @author marhali
 */
public record RemoveI18nValueCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key,
    @NotNull LocaleId localeId
    ) implements Command {
}
