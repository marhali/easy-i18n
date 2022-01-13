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
 * Action which toggles translation filter on missing values.
 * @author marhali
 */
public class FilterMissingTranslationsAction extends AnAction {
    public FilterMissingTranslationsAction() {
        super(ResourceBundle.getBundle("messages").getString("action.toggle-missing"),
                null, AllIcons.General.ShowWarning);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        boolean enable = e.getPresentation().getIcon() == AllIcons.General.ShowWarning;
        e.getPresentation().setIcon(enable ? AllIcons.General.Warning : AllIcons.General.ShowWarning);
        InstanceManager.get(project).bus().propagate().onFilterMissingTranslations(enable);
    }
}