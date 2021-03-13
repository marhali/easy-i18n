package de.marhali.easyi18n.util;

import java.util.Map;

/**
 * User interface utilities.
 * @author marhali
 */
public class UiUtil {

    public static String generateHtmlTooltip(Map<String, String> messages) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");

        for(Map.Entry<String, String> entry : messages.entrySet()) {
            builder.append("<b>");
            builder.append(entry.getKey()).append(":");
            builder.append("</b> ");
            builder.append(entry.getValue());
            builder.append("<br>");
        }

        builder.append("</html>");

        return builder.toString();
    }
}