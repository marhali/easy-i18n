package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import de.marhali.easyi18n.config.project.ProjectConfigService;
import de.marhali.easyi18n.next_io.PersistenceHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

/**
 * Reload translations action.
 * @author marhali
 */
public class ReloadAction extends AnAction {

    public ReloadAction() {
        super(ResourceBundle.getBundle("messages").getString("action.reload"),
                null, AllIcons.Actions.Refresh);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //InstanceManager.get(Objects.requireNonNull(e.getProject())).reload();
        // Just a small button to trigger our new IO system

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
