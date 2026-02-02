package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Factory to construct {@link ProjectId ProjectId}.
 *
 * @author marhali
 */
public final class ProjectIdFactory {
    private ProjectIdFactory() {}

    public static @NotNull ProjectId from(@NotNull Project project) {
        return new ProjectId("idea:" + project.getLocationHash());
    }
}
