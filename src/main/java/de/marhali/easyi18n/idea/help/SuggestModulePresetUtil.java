package de.marhali.easyi18n.idea.help;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import org.jetbrains.annotations.NotNull;

/**
 * @author marhali
 */
public final class SuggestModulePresetUtil {

    private SuggestModulePresetUtil() {}

    public static void suggest(@NotNull Project project, @NotNull ProjectConfigModule moduleConfig) {
        IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(PluginId.getId("de.marhali.easyi18n"));

        StringBuilder bodyBuilder = new StringBuilder();

        bodyBuilder.append("# Summary\n");
        bodyBuilder.append("!!! Please describe the intention of this preset !!!\n");

        bodyBuilder.append("# Preset\n");
        PrettyConfigUtil.appendModuleConfig(bodyBuilder, moduleConfig, project);

        bodyBuilder.append("\nEOF");

        String title = "IDE Suggest Module Preset (v" + (plugin != null ? plugin.getVersion() : "???") + ")";
        String labels = "ide suggest";
        String body = bodyBuilder.toString();

        String url = WebUtil.createOpenGitHubIssueUrl(title, labels, body);

        new Task.Backgroundable(project, "Open GitHub issue", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                BrowserUtil.browse(url);
            }
        }.queue();
    }
}
