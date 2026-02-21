package de.marhali.easyi18n.core.application.command;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Command to reload an optionally specified module or (if not specified) everything.
 *
 * @param moduleId Optional module identifier
 */
public record ReloadCommand(
    @Nullable ModuleId moduleId
    ) implements Command {
    /**
     * Creates a reload command for the entire project.
     *
     * @return {@link ReloadCommand}
     */
    public static @NotNull ReloadCommand reloadAll() {
        return new ReloadCommand(null);
    }
}
