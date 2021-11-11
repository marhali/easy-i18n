package de.marhali.easyi18n.io.yaml;

import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;

import thito.nodeflow.config.ListSection;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;

import java.util.Map;

/**
 * Mapper for mapping yaml files into translation nodes and backwards.
 * @author marhali
 */
public class YamlMapper {

    public static void read(String locale, Section section, TranslationNode node) {
        for(String key : section.getKeys()) {
            Object value = section.getInScope(key).get();

            TranslationNode childNode = node.getOrCreateChildren(key);

            if(value instanceof MapSection) {
                // Nested element - run recursively
                read(locale, (MapSection) value, childNode);
            } else {
                Translation translation = childNode.getValue();

                String content = value instanceof ListSection
                        ? YamlArrayMapper.read((ListSection) value)
                        : StringUtil.escapeControls(String.valueOf(value), true);

                translation.put(locale, content);
                childNode.setValue(translation);
            }
        }
    }

    public static void write(String locale, Section section, TranslationNode node) {
        for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childNode = entry.getValue();

            if(!childNode.isLeaf()) {
                // Nested node - run recursively
                MapSection childSection = new MapSection();
                write(locale, childSection, childNode);
                if(childSection.size() > 0) {
                    section.setInScope(key, childSection);
                }
            } else {
                Translation translation = childNode.getValue();
                String content = translation.get(locale);

                if(content != null) {
                    if(YamlArrayMapper.isArray(content)) {
                        section.setInScope(key, YamlArrayMapper.write(content));
                    } else if(NumberUtils.isNumber(content)) {
                        section.setInScope(key, NumberUtils.createNumber(content));
                    } else {
                        section.setInScope(key, StringEscapeUtils.unescapeJava(content));
                    }
                }
            }
        }
    }
}
