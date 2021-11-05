package de.marhali.easyi18n.io.properties;

import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Map;

/**
 * Mapper for mapping properties files into translation nodes and backwards.
 * @author marhali
 */
public class PropertiesMapper {

    // TODO: support array values

    public static void read(String locale, SortableProperties properties, TranslationData data) {
        for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = String.valueOf(entry.getKey());
            String content = StringUtil.escapeControls(String.valueOf(entry.getValue()), true);

            Translation translation = data.getTranslation(key);

            if(translation == null) {
                translation = new Translation();
            }

            translation.put(locale, content);
        }
    }

    public static void write(String locale, SortableProperties properties, TranslationData data) {
        for(String key : data.getFullKeys()) {
            Translation translation = data.getTranslation(key);

            if(translation != null && translation.containsKey(locale)) {
                String content = StringEscapeUtils.unescapeJava(translation.get(locale));
                properties.put(key, content);
            }
        }
    }
}
