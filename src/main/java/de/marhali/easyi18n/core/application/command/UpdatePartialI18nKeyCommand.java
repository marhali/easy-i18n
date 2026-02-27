package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Command to partially update the {@link de.marhali.easyi18n.core.domain.model.I18nKey} for all matching translations.
 *
 * @param moduleId Module identifier.
 * @param parentKeyParts Parent key parts
 * @param previousKeyPart Previous key part that should be replaced
 * @param newKeyPart New value for the key part
 *
 * @author marhali
 */
public record UpdatePartialI18nKeyCommand(
    @NotNull ModuleId moduleId,
    @NotNull List<@NotNull String> parentKeyParts,
    @NotNull String previousKeyPart,
    @NotNull String newKeyPart
    ) implements Command {
}
