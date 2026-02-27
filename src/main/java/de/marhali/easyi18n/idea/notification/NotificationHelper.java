package de.marhali.easyi18n.idea.notification;

import de.marhali.easyi18n.idea.messages.PluginBundle;
import org.jetbrains.annotations.NotNull;

/**
 * Notification helper utilities.
 *
 * @author marhali
 */
public final class NotificationHelper {

    private NotificationHelper() {}

    public static @NotNull String getThrowableAsHtmlNotification(@NotNull Throwable throwable) {
        return PluginBundle.message("error.generic.throwable", throwable.getMessage(), "?createIssue");
    }
}
