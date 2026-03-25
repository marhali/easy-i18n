package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.idea.config.ProjectConfigConfigurable;
import de.marhali.easyi18n.idea.config.ShowProjectConfigUtil;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Action to open the plugin's {@link ProjectConfigConfigurable config} dialog.
 *
 * @author marhali
 */
public final class OpenProjectConfigAction extends DumbAwareAction {

    public OpenProjectConfigAction(boolean showIcon) {
        super(PluginBundle.message("action.show.config.project.label"), null, showIcon ? AllIcons.General.Settings : null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Objects.requireNonNull(e.getProject(), "Project must not be null");
        ShowProjectConfigUtil.open(e.getProject());
    }
}
