package de.marhali.easyi18n.io.parser.properties;

import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.util.KeyPathConverter;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;

/**
 * Mapper for mapping properties files into translation nodes and backwards.
 * @author marhali
 */
public class PropertiesMapper {

    public static void read(String locale, SortableProperties properties,
                            TranslationData data, KeyPathConverter converter) {

        for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            KeyPath key = converter.fromString(String.valueOf(entry.getKey()));
            Object value = entry.getValue();

            TranslationValue translation = data.getTranslation(key);

            if(translation == null) {
                translation = new TranslationValue();
            }

            String content = value instanceof String[]
                    ? PropertiesArrayMapper.read((String[]) value)
                    : StringUtil.escapeControls(String.valueOf(value), true);

            translation.put(locale, content);
            data.setTranslation(key, translation);
        }
    }

    public static void write(String locale, SortableProperties properties,
                             TranslationData data, KeyPathConverter converter, boolean isSaveAsStrings) {

        for(KeyPath key : data.getFullKeys()) {
            TranslationValue translation = data.getTranslation(key);

            if(translation != null && translation.containsLocale(locale)) {
                String simpleKey = converter.toString(key);
                String content = translation.get(locale);

                if(PropertiesArrayMapper.isArray(content)) {
                    properties.put(simpleKey, PropertiesArrayMapper.write(content));
                } else if(!isSaveAsStrings && NumberUtils.isCreatable(content)) {
                    properties.put(simpleKey, NumberUtils.createNumber(content));
                } else {
                    properties.put(simpleKey, StringEscapeUtils.unescapeJava(content));
                }
            }
        }
    }
}
