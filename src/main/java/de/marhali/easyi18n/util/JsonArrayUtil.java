package de.marhali.easyi18n.util;

import com.google.gson.JsonArray;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.regex.Pattern;

/**
 * Utility methods to read and write json arrays.
 * @author marhali
 */
public class JsonArrayUtil {

    public static String ARRAY_PREFIX = "!arr[";
    public static String ARRAY_SUFFIX = "]";
    public static char ARRAY_DELIMITER = ';';

    public static String read(JsonArray array) {
        StringBuilder builder = new StringBuilder(ARRAY_PREFIX);

        for(int i = 0; i < array.size(); i++) {
            if(i > 0) {
                builder.append(ARRAY_DELIMITER);
            }

            String value = array.get(i).getAsString().replace(";", "\\;");
            builder.append(StringUtil.escapeControls(value, true));
        }

        builder.append(ARRAY_SUFFIX);
        return builder.toString();
    }

    public static JsonArray write(String concat) {
        concat = concat.substring(ARRAY_PREFIX.length(), concat.length() - ARRAY_SUFFIX.length());
        String regex = "(?<!\\\\)" + Pattern.quote(String.valueOf(ARRAY_DELIMITER));

        JsonArray array = new JsonArray();

        for(String element : concat.split(regex)) {
            element = element.replace("\\" + ARRAY_DELIMITER, String.valueOf(ARRAY_DELIMITER));
            array.add(StringEscapeUtils.unescapeJava(element));
        }

        return array;
    }

    public static boolean isArray(String concat) {
        return concat != null && concat.startsWith(ARRAY_PREFIX) && concat.endsWith(ARRAY_SUFFIX);
    }
}
