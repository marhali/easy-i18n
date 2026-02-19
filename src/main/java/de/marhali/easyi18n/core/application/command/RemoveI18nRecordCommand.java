package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to remove an existing translation.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 *
 * @author marhali
 */
public record RemoveI18nRecordCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key
    ) implements Command {
}
