package de.marhali.easyi18n.util;

import java.util.Map;
import java.util.Set;

/**
 * User interface utilities.
 * @author marhali
 */
public class UiUtil {

    /**
     * Generates a html compliant string which shows all defined translations
     * @param messages Contains locales with desired translation
     * @return String with html format
     */
    public static String generateHtmlTooltip(Set<Map.Entry<String, String>> messages) {
        StringBuilder builder = new StringBuilder();

        builder.append("<html>");

        for(Map.Entry<String, String> entry : messages) {
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