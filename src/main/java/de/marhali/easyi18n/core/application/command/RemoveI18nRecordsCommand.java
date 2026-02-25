package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Command to remove a set of translation records.
 *
 * @param moduleId Module identifier
 * @param keys Set of translation keys to remove
 *
 * @author marhali
 */
public record RemoveI18nRecordsCommand(
    @NotNull ModuleId moduleId,
    @NotNull Set<@NotNull I18nKey> keys
) implements Command { }
