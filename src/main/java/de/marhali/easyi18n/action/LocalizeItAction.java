package de.marhali.easyi18n.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.dialog.AddDialog;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.DocumentUtil;
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
        if (editor == null) return;
        String text = editor.getSelectionModel().getSelectedText();

        if (text == null || text.isEmpty()) return;


        if ((text.startsWith("\"") && text.endsWith("\"")) || (text.startsWith("'") && text.endsWith("'"))) {
            text = text.substring(1);
            text = text.substring(0, text.length() - 1);

        }
        Project project = anActionEvent.getProject();
        if (project == null) {
            throw new RuntimeException("Project is null!");
        }

        AddDialog dialog = new AddDialog(project, new KeyPath(text), text, (key) -> replaceSelectedText(project, editor, key));
        dialog.showAndHandle();


    }

    private void replaceSelectedText(Project project, @NotNull Editor editor, @NotNull String key) {
        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        String flavorTemplate = ProjectSettingsService.get(project).getState().getFlavorTemplate();
        DocumentUtil documentUtil = new DocumentUtil(editor.getDocument());
        String replacement = buildReplacement(flavorTemplate, key, documentUtil);
        WriteCommandAction.runWriteCommandAction(editor.getProject(), () -> documentUtil.getDocument().replaceString(selectionStart, selectionEnd, replacement));
    }

    private String buildReplacement(String flavorTemplate, String key, DocumentUtil documentUtil) {
        if (documentUtil.isVue() || documentUtil.isJsOrTs()) return flavorTemplate + "('" + key + "')";

        return flavorTemplate + "(\"" + key + "\")";
    }
}