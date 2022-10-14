package de.marhali.easyi18n.action;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Plugin action to open a specific file.
 * @author marhali
 */
public class OpenFileAction extends AnAction {
    private final VirtualFile file;

    public OpenFileAction(VirtualFile file) {
        this(file, true);
    }

    public OpenFileAction(VirtualFile file, boolean showIcon) {
        super(ResourceBundle.getBundle("messages").getString("action.file"),
                null, showIcon ? AllIcons.FileTypes.Any_type : null);
        this.file = file;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        FileEditorManager.getInstance(project).openFile(file, true, true);
    }
}
