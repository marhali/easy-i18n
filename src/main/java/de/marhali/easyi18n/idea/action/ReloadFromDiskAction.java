package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.config.project.ProjectConfigService;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.next_io.PersistenceHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Action to reload from disk.
 *
 * @author marhali
 */
public final class ReloadFromDiskAction extends DumbAwareAction {

    public ReloadFromDiskAction() {
        super(PluginBundle.message("action.reload.label"), null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var persi = new PersistenceHandler(e.getProject(), ProjectConfigService.forProject(e.getProject()).getState());
        try {
            var store = persi.read();
            System.out.println(store);
            System.out.println("----");
            persi.write(store);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
