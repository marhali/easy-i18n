package de.marhali.easyi18n.core.application.cqrs.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Command to update the translation key of an existing translation.
 *
 * @param moduleId Module identifier
 * @param oldKey Old translation key
 * @param newKey New translation key
 *
 * @author marhali
 */
public record UpdateI18nKeyCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey oldKey,
    @NotNull I18nKey newKey
    ) implements Command {
}
