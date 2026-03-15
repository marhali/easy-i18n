package de.marhali.easyi18n.idea.help;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.idea.config.ProjectConfigService;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Error report submitter which opens a link to create a prefiled issue on GitHub.
 *
 * @author marhali
 */
public class PluginErrorReportSubmitter extends ErrorReportSubmitter {
    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return PluginBundle.message("error.submit.github");
    }

    @Override
    public boolean submit(@NotNull IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {
        Project project = DataManager.getInstance().getDataContext(parentComponent).getData(CommonDataKeys.PROJECT);

        if (project == null) {
            throw new IllegalStateException("Could not retrieve project from parentComponent data context");
        }

        String version = getPluginDescriptor().getVersion();
        ProjectConfig config = project.getService(ProjectConfigService.class).getDomainState();

        StringBuilder bodyBuilder = new StringBuilder();

        bodyBuilder.append("# Description\n");

        if (additionalInfo != null) {
            bodyBuilder.append(additionalInfo).append("\n");
        } else {
            bodyBuilder.append("!!! Please describe the issue !!!\n");
        }

        PrettyConfigUtil.appendConfig(bodyBuilder, config, project);

        for (int i = 0; i < events.length; i++) {
            var event = events[i];

            bodyBuilder.append("# Exception #").append(i).append("\n");

            if (event.getMessage() != null) {
                bodyBuilder.append(events[i].getMessage()).append("\n");
            } else {
                bodyBuilder.append("No message\n");
            }

            bodyBuilder.append("```\n").append(events[i].getThrowableText()).append("\n```\n");
        }

        bodyBuilder.append("EOF");

        String title = "IDE Error Report (v" + version + ")";
        String labels = "ide report";
        String body = bodyBuilder.toString();

        String url = WebUtil.createOpenGitHubIssueUrl(title, labels, body);

        new Task.Backgroundable(project, "Open GitHub issue", false) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                progressIndicator.setIndeterminate(true);

                BrowserUtil.browse(url);

                ApplicationManager.getApplication().invokeLater(() -> {
                    consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE));
                });
            }
        }.queue();

        return true;
    }
}
