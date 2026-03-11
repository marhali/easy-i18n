package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorElement;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Service to resolve the associated translation module for {@link EditorElement}'s.
 *
 * @author marhali
 */
public class EditorElementModuleResolver {

    private final @NotNull ProjectConfigPort projectConfigPort;

    public EditorElementModuleResolver(@NotNull ProjectConfigPort projectConfigPort) {
        this.projectConfigPort = projectConfigPort;
    }

    /**
     * Resolves the associated module identifier for the given editor element.
     * @param editorElement Editor element
     * @return {@link ModuleId} or {@code null} if the editor element cannot be associated with any translation module
     */
    public @Nullable ModuleId resolve(@NotNull EditorElement editorElement) {
        String filePath = editorElement.filePath();

        if (filePath == null) {
            return null;
        }

        for (Map.Entry<@NotNull ModuleId, @NotNull ProjectConfigModule> entry : projectConfigPort.read().modules().entrySet()) {
            if (filePath.startsWith(entry.getValue().rootDirectory())) {
                // First match wins
                return entry.getKey();
            }
        }

        return null;
    }
}
