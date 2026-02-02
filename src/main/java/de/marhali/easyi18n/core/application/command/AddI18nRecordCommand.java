package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Command to add a new translation with multiple localized values.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 * @param entries Translation values
 */
public record AddI18nRecordCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key,
    @NotNull Map<@NotNull LocaleId, @NotNull I18nValue> entries
    ) implements Command {
}
