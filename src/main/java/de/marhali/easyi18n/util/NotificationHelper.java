package de.marhali.easyi18n.util;

import com.intellij.notification.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.action.SettingsAction;
import de.marhali.easyi18n.io.IOHandler;
import de.marhali.easyi18n.settings.ProjectSettings;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility tool to support creating notifications with detailed information like exception traces.
 * @author marhali
 */
public class NotificationHelper {

    public static void createIOError(@NotNull ProjectSettings state, Exception ex) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        String message = MessageFormat.format(bundle.getString("error.io"),
                state.getFolderStrategy(), state.getParserStrategy(), state.getFilePattern(), state.getLocalesDirectory());

        Logger.getInstance(IOHandler.class).error(message, ex);
    }

    public static void createEmptyLocalesDirNotification(Project project) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        Notification notification = new Notification(
                "Easy I18n Notification Group",
                "Easy I18n",
                bundle.getString("warning.missing-config"),
                NotificationType.WARNING);

        notification.addAction(new SettingsAction());

        Notifications.Bus.notify(notification, project);
    }

    public static void createTranslatorServiceErrorNotification(Project project, int statusCode, String response) {
        final ResourceBundle bundle = ResourceBundle.getBundle("messages");

        final String message = String.format("%s%nStatus %d: %s", bundle.getString("error.translator-error"), statusCode, response);
        final Notification notification = new Notification("Easy I18n Notification Group", "Easy I18n",
                message, NotificationType.ERROR);

        Notifications.Bus.notify(notification, project);
    }
}