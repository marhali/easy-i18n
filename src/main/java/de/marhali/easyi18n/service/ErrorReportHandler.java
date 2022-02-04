package de.marhali.easyi18n.service;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Easily create issues on the project repository if exceptions occur.
 * @author marhali
 */
public class ErrorReportHandler extends ErrorReportSubmitter {
    @Override
    public @NlsActions.ActionText @NotNull String getReportActionText() {
        return ResourceBundle.getBundle("messages").getString("error.submit");
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo,
                          @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {

        if(events.length == 0) {
            return false;
        }

        IdeaLoggingEvent event = events[0];

        DataManager mgr = DataManager.getInstance();
        DataContext context = mgr.getDataContext(parentComponent);
        Project project = CommonDataKeys.PROJECT.getData(context);

        String title = "IDE Error Report";
        String labels = "ide report";
        String body = "# Additional information\n"
                + additionalInfo + "\n"
                + "# Exception trace\n"
                + event.getMessage() + "\n"
                + "```\n"
                + event.getThrowableText()
                + "\n```";

        String url = "https://github.com/marhali/easy-i18n/issues/new?title="
                + encodeParam(title) + "&labels=" + encodeParam(labels) + "&body=" + encodeParam(body);

        new Task.Backgroundable(project, "Sending error report") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                BrowserUtil.browse(url);

                ApplicationManager.getApplication().invokeLater(() ->
                        consumer.consume(new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE)));
            }
        }.queue();

        return true;
    }

    private String encodeParam(String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
