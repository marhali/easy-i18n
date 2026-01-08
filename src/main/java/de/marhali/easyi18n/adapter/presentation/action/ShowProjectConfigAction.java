package de.marhali.easyi18n.adapter.presentation.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.adapter.presentation.messages.PluginBundle;
import de.marhali.easyi18n.config.project.ProjectConfigConfigurable;
import org.jetbrains.annotations.NotNull;

/**
 * Action to open the plugin's {@link ProjectConfigConfigurable config} page.
 *
 * @author marhali
 */
public class ShowProjectConfigAction extends DumbAwareAction {

    public ShowProjectConfigAction(boolean showIcon) {
        super(PluginBundle.message("action.show.config.project"), null, showIcon ? AllIcons.General.Settings : null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.getProject(), ProjectConfigConfigurable.class);
    }
}
