package de.marhali.easyi18n.io.yaml;

import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.math.NumberUtils;

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
            TranslationNode childNode = node.getOrCreateChildren(key);

            if(section.getMap(key).isPresent()) {
                // Nested element - run recursively
                read(locale, section.getMap(key).get(), childNode);
            } else {
                Translation translation = childNode.getValue();

                if(section.getList(key).isPresent() || section.getString(key).isPresent()) {
                    String content = section.isList(key) && section.getList(key).isPresent()
                            ? YamlArrayMapper.read(section.getList(key).get())
                            : StringUtil.escapeControls(section.getString(key).get(), true);

                    translation.put(locale, content);
                    childNode.setValue(translation);
                }
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
                    section.set(key, childSection);
                }
            } else {
                Translation translation = childNode.getValue();
                String content = translation.get(locale);

                if(content != null) {
                    if(YamlArrayMapper.isArray(content)) {
                        section.set(key, YamlArrayMapper.write(content));
                    } else if(NumberUtils.isNumber(content)) {
                        section.set(key, NumberUtils.createNumber(content));
                    } else {
                        section.set(key, StringEscapeUtils.unescapeJava(content));
                    }
                }
            }
        }
    }
}
