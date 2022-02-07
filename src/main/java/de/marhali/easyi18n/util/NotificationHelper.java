package de.marhali.easyi18n.util;

import com.intellij.notification.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.action.SettingsAction;
import de.marhali.easyi18n.io.IOHandler;
import de.marhali.easyi18n.model.SettingsState;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility tool to support creating notifications with detailed information like exception traces.
 * @author marhali
 */
public class NotificationHelper {

    public static void createIOError(@NotNull SettingsState state, Exception ex) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        String message = MessageFormat.format(bundle.getString("error.io"),
                state.getFolderStrategy(), state.getParserStrategy(), state.getFilePattern(), state.getLocalesPath());

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
}