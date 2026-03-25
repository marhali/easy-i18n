package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.I18nContent;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

public record UpdateI18nRecordCommand(
    @NotNull ModuleId moduleId,
    @NotNull I18nKey originKey,
    @NotNull I18nKey key,
    @NotNull I18nContent content
    ) implements Command { }
