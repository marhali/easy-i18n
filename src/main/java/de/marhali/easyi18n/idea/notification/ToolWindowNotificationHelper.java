package de.marhali.easyi18n.idea.notification;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.wm.ToolWindowManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.HyperlinkEvent;

/**
 * @author marhali
 */
public final class ToolWindowNotificationHelper {

    private static final @NotNull Logger LOGGER = Logger.getInstance(ToolWindowNotificationHelper.class);

    private ToolWindowNotificationHelper() {}

    /**
     * Opens a tool window specific notification balloon about the given throwable.
     * @param project Opened project
     * @param toolWindowId Tool window identifier
     * @param throwable Thrown error
     */
    public static void showNotificationForThrowable(
        @NotNull Project project,
        @NotNull String toolWindowId,
        @NotNull Throwable throwable
    ) {
        ToolWindowManager.getInstance(project).notifyByBalloon(
            toolWindowId,
            MessageType.ERROR,
            NotificationHelper.getThrowableAsHtmlNotification(throwable),
            null,
            (e) -> {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    LOGGER.error(throwable);
                }
            }
        );
    }
}
