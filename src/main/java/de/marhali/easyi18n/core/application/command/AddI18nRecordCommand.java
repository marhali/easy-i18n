package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Command to add a new translation with multiple localized values.
 *
 * @param moduleId Module identifier
 * @param key Translation key
 * @param content Translation content
 */
public record AddI18nRecordCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey key,
    @NotNull I18nContent content
    ) implements Command {
}
