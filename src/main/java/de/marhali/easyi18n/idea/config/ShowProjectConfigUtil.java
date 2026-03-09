package de.marhali.easyi18n.idea.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * Show project config utilities.
 *
 * @author marhali
 */
public final class ShowProjectConfigUtil {

    private ShowProjectConfigUtil() {}

    /**
     * Opens the project configuration page
     * @param project Opened project
     */
    public static void open(@NotNull Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (!project.isDisposed()) {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectConfigConfigurable.class);
            }
        });
    }
}
