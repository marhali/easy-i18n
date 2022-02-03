package de.marhali.easyi18n.util;

import com.intellij.openapi.diagnostic.Logger;
import de.marhali.easyi18n.ionext.IOHandler;
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
}