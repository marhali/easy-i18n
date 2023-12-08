package de.marhali.easyi18n.io.parser;

import de.marhali.easyi18n.util.StringUtil;
import org.apache.commons.text.StringEscapeUtils;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Simple array support for translation values.
 * Some i18n systems allows the user to define array values for some translations.
 * We support array values by wrapping them into: '!arr[valueA;valueB]'.
 *
 * @author marhali
 */
public abstract class ArrayMapper {
    static final String PREFIX = "!arr[";
    static final String SUFFIX = "]";
    static final char DELIMITER = ';';

    public static final String SPLITERATOR_REGEX =
            MessageFormat.format("(?<!\\\\){0}", Pattern.quote(String.valueOf(DELIMITER)));

    protected static <T> String read(Iterator<T> elements, Function<T, String> stringFactory) {
        StringBuilder builder = new StringBuilder(PREFIX);

        int i = 0;
        while(elements.hasNext()) {
            if(i > 0) {
                builder.append(DELIMITER);
            }

            String value = stringFactory.apply(elements.next());

            builder.append(StringUtil.escapeControls(
                    value.replace(String.valueOf(DELIMITER), "\\" + DELIMITER), true));

            i++;
        }

        builder.append(SUFFIX);
        return builder.toString();
    }

    protected static void write(String concat, Consumer<String> writeElement) {
        concat = concat.substring(PREFIX.length(), concat.length() - SUFFIX.length());

        for(String element : concat.split(SPLITERATOR_REGEX)) {
            element = element.replace("\\" + DELIMITER, String.valueOf(DELIMITER));
            writeElement.accept(StringEscapeUtils.unescapeJava(element));
        }
    }

    public static boolean isArray(String concat) {
        return concat != null && concat.startsWith(PREFIX) && concat.endsWith(SUFFIX);
    }
}