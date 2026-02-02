package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Action to collapse all expanded elements.
 *
 * @author marhali
 */
public class CollapseAllAction extends DumbAwareAction {

    private final @NotNull Runnable onHandleCollapse;

    public CollapseAllAction(@NotNull Runnable onHandleCollapse) {
        super(PluginBundle.message("action.view.collapse.label"), null, AllIcons.Actions.Collapseall);

        this.onHandleCollapse = onHandleCollapse;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        onHandleCollapse.run();
    }
}
