package de.marhali.easyi18n.idea.config.component;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.ActionLink;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionListener;

/**
 * @author marhali
 */
public class ProjectConfigIntroUi extends ConfigComponent<FormBuilder, ProjectConfig, ProjectConfigBuilder> {
    protected ProjectConfigIntroUi(@NotNull Project project) {
        super(project);
    }

    @Override
    public void buildComponent(@NotNull FormBuilder builder) {
        builder.addComponent(new JBLabel(PluginBundle.message("config.project.intro.description")));
        builder.addComponent(new ActionLink(
            PluginBundle.message("config.project.intro.documentation-link"),
            (ActionListener) (event) -> BrowserUtil.browse("https://github.com/marhali/easy-i18n"))
        );
    }

    @Override
    public boolean isModified(@NotNull ProjectConfig originState) {
        // This is a stateless child
        return false;
    }

    @Override
    public void writeStateToComponent(@NotNull ProjectConfig projectConfig) {
        // This is a stateless child
    }

    @Override
    public void readStateFromComponent(@NotNull ProjectConfigBuilder builder) {
        // This is a stateless child
    }
}
