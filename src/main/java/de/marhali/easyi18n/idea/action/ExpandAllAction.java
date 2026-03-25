package de.marhali.easyi18n.idea.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Action to expand all collapsed elements.
 *
 * @author marhali
 */
public class ExpandAllAction extends DumbAwareAction {

    private final @NotNull Runnable onHandleExpand;

    public ExpandAllAction(@NotNull Runnable onHandleExpand) {
        super(PluginBundle.message("action.view.expand.label"), null, AllIcons.Actions.Expandall);

        this.onHandleExpand = onHandleExpand;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        onHandleExpand.run();
    }
}
