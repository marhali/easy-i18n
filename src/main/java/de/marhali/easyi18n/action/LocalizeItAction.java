package de.marhali.easyi18n.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.model.KeyPath;
import org.jetbrains.annotations.NotNull;

/**
 * The LocalizeItAction class represents an IntelliJ IDEA action that localizes selected text.
 *
 * <p>When this action is performed, it retrieves the selected text from the editor, checks if it is not empty,
 * and then displays a dialog to add the selected text as a localized string key to the project.
 *
 * <p>If the selected text is empty or the project is null, the action does nothing.
 *
 * <p>This class extends the AnAction class provided by IntelliJ IDEA.
 */
class LocalizeItAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if (editor == null)
            return;
        String text = editor.getSelectionModel().getSelectedText();
        if (text == null || text.isEmpty())
            return;

        Project project = anActionEvent.getProject();
        if (project == null) {
            throw new RuntimeException("Project is null!");
        }

        AddDialog dialog = new AddDialog(project, new KeyPath(text), text);
        dialog.showAndHandle();
    }
}