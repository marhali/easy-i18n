package de.marhali.easyi18n.io.parser.yaml;

import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlMapper {
    @SuppressWarnings("unchecked")
    public static void read(String locale, Map<String, Object> section, TranslationNode node) {
        for(String key : section.keySet()) {
            Object value = section.get(key);
            TranslationNode childNode = node.getOrCreateChildren(key);

            if(value instanceof Map) {
                // Nested element run recursively
                read(locale, (Map<String, Object>) value, childNode);
            } else {
                TranslationValue translation = childNode.getValue();

                String content = value instanceof List
                        ? YamlArrayMapper.read((List<Object>) value)
                        : StringUtil.escapeControls(String.valueOf(value), true);

                translation.put(locale, content);
                childNode.setValue(translation);
            }
        }
    }

    public static void write(String locale, Map<String, Object> section, TranslationNode node, boolean isSaveAsStrings) {
        for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childNode = entry.getValue();

            if(!childNode.isLeaf()) {
                // Nested node - run recursively
                Map<String, Object> childSection = new HashMap<>();
                write(locale, childSection, childNode, isSaveAsStrings);
                if(!childSection.isEmpty()) {
                    section.put(key, childSection);
                }
            } else {
                TranslationValue translation = childNode.getValue();
                String content = translation.get(locale);

                if(content != null) {
                    if(YamlArrayMapper.isArray(content)) {
                        section.put(key, YamlArrayMapper.write(content));
                    } else if(!isSaveAsStrings && NumberUtils.isCreatable(content)) {
                        section.put(key, NumberUtils.createNumber(content));
                    } else {
                        section.put(key, StringEscapeUtils.unescapeJava(content));
                    }
                }
            }
        }
    }
}
