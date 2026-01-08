package de.marhali.easyi18n.adapter.presentation.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import de.marhali.easyi18n.adapter.presentation.action.ShowProjectConfigAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author marhali
 */
public class I18nToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        toolWindow.setTitleActions(List.of(
            new ToggleAction("Show As Tree", null, AllIcons.General.Tree) {
                @Override
                public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
                    return true;
                }

                @Override
                public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {

                }
            },
        new ShowProjectConfigAction(true)
        ));
    }
}
