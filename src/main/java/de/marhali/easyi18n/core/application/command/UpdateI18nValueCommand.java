package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to update an existing localized value from a translation.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 * @param localeId Locale identifier
 * @param newValue New translation value
 *
 * @author marhali
 */
public record UpdateI18nValueCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key,
    @NotNull LocaleId localeId,
    @NotNull I18nValue newValue
    ) implements Command {
}
