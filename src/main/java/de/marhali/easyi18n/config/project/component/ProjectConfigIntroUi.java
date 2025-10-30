package de.marhali.easyi18n.config.project.component;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.project.ProjectConfig;

import java.awt.event.ActionListener;

/**
 * @author marhali
 */
public class ProjectConfigIntroUi extends BaseProjectConfigUi{
    protected ProjectConfigIntroUi(Project project) {
        super(project);
    }

    @Override
    public void buildComponent(FormBuilder builder) {
        builder.addComponent(new JBLabel(i18n.getString("config.project.intro.description")));
        builder.addComponent(new ActionLink(i18n.getString("config.project.intro.documentation-link"), (ActionListener) (event) -> BrowserUtil.browse("https://github.com/marhali/easy-i18n")));
    }

    @Override
    public boolean isModified() {
        // This is a stateless child
        return false;
    }

    @Override
    public void applyChangesToState() {
        // This is a stateless child
    }

    @Override
    public void applyStateToComponent(ProjectConfig state) {
        super.applyStateToComponent(state);
        // This is a stateless child
    }
}
