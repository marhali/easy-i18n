package de.marhali.easyi18n.util;

import com.intellij.openapi.diagnostic.Logger;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Utility tool to support creating notifications with detailed information like exception traces.
 * @author marhali
 */
public class NotificationHelper {
    public static void createIOError(String fileName, Class<?> ioStrategy, Exception ex) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        String message = MessageFormat.format(bundle.getString("error.io"), fileName, ioStrategy.getSimpleName());
        Logger.getInstance(ioStrategy).error(message, ex.getCause());
    }
}