package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.InstanceManager;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Action to toggle duplicate translation values filter.
 * @author marhali
 */
public class FilterDuplicateAction extends AnAction {
    public FilterDuplicateAction() {
        super(ResourceBundle.getBundle("messages").getString("action.filter.duplicate"),
                null, AllIcons.Actions.PreserveCase);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        boolean enable = e.getPresentation().getIcon() == AllIcons.Actions.PreserveCase;
        e.getPresentation().setIcon(enable ? AllIcons.Actions.PreserveCaseSelected : AllIcons.Actions.PreserveCase);
        InstanceManager.get(project).bus().propagate().onFilterDuplicate(enable);
    }
}
