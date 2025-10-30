package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.config.project.ProjectConfig;

/**
 * @author marhali
 */
public abstract class BaseProjectConfigUi extends AbstractProjectConfigUi<ProjectConfig> {
    protected BaseProjectConfigUi(Project project) {
        super(project);
    }
}
