package de.marhali.easyi18n.ionext.parser.properties;

import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Map;

/**
 * Mapper for mapping properties files into translation nodes and backwards.
 * @author marhali
 */
public class PropertiesMapper {

    public static void read(String locale, SortableProperties properties, TranslationData data) {
        for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            KeyPath key = new KeyPath(String.valueOf(entry.getKey()));
            Object value = entry.getValue();

            Translation translation = data.getTranslation(key);

            if(translation == null) {
                translation = new Translation();
            }

            String content = value instanceof String[]
                    ? PropertiesArrayMapper.read((String[]) value)
                    : StringUtil.escapeControls(String.valueOf(value), true);

            translation.put(locale, content);
            data.setTranslation(key, translation);
        }
    }

    public static void write(String locale, SortableProperties properties, TranslationData data) {
        for(KeyPath key : data.getFullKeys()) {
            Translation translation = data.getTranslation(key);

            if(translation != null && translation.containsKey(locale)) {
                String simpleKey = key.toSimpleString();
                String content = translation.get(locale);

                if(PropertiesArrayMapper.isArray(content)) {
                    properties.put(simpleKey, PropertiesArrayMapper.write(content));
                } else if(NumberUtils.isNumber(content)) {
                    properties.put(simpleKey, NumberUtils.createNumber(content));
                } else {
                    properties.put(simpleKey, StringEscapeUtils.unescapeJava(content));
                }
            }
        }
    }
}
