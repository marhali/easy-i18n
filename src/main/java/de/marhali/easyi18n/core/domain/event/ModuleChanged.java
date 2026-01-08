package de.marhali.easyi18n.core.domain.event;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record ModuleChanged(
    @NotNull ModuleId moduleId,
    @NotNull Set<I18nKey> changedKeys
    ) implements DomainEvent {
}
