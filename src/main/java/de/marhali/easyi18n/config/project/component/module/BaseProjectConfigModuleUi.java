package de.marhali.easyi18n.config.project.component.module;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.config.project.component.AbstractProjectConfigUi;

/**
 * @author marhali
 */
public abstract class BaseProjectConfigModuleUi extends AbstractProjectConfigUi<ProjectConfigModule> {
    protected BaseProjectConfigModuleUi(Project project) {
        super(project);
    }
}
