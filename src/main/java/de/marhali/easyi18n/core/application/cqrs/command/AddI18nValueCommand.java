package de.marhali.easyi18n.core.application.cqrs.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to add a new localized value to a translation.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 * @param localeId Locale identifier
 * @param value Translation value
 *
 * @author marhali
 */
public record AddI18nValueCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key,
    @NotNull LocaleId localeId,
    @NotNull I18nValue value
    ) implements Command {
}
