package de.marhali.easyi18n.assistance;

import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.settings.ProjectSettingsService;

import org.jetbrains.annotations.NotNull;

/**
 * Used to define editor hooks as assistable.
 * @author marhali
 */
public interface OptionalAssistance {
    default boolean isAssistance(@NotNull Project project) {
        return ProjectSettingsService.get(project).getState().isAssistance();
    }
}
