package de.marhali.easyi18n.io.parser.json5;

import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.util.StringUtil;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import de.marhali.json5.Json5Primitive;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;

/**
 * Mapper for mapping json5 objects into translation nodes and backwards.
 * @author marhali
 */
public class Json5Mapper {
    public static void read(String locale, Json5Object json, TranslationNode node) {
        for(Map.Entry<String, Json5Element> entry : json.entrySet()) {
            String key = entry.getKey();
            Json5Element value = entry.getValue();

            TranslationNode childNode = node.getOrCreateChildren(key);

            if(value.isJson5Object()) {
                // Nested element - run recursively
                read(locale, value.getAsJson5Object(), childNode);
            } else {
                TranslationValue translation = childNode.getValue();

                String content = value.isJson5Array()
                        ? Json5ArrayMapper.read(value.getAsJson5Array())
                        : StringUtil.escapeControls(value.getAsString(), true);

                translation.put(locale, content);
                childNode.setValue(translation);
            }
        }
    }

    public static void write(String locale, Json5Object json, TranslationNode node, boolean isSaveAsStrings) {
        for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childNode = entry.getValue();

            if(!childNode.isLeaf()) {
                // Nested node - run recursively
                Json5Object childJson = new Json5Object();
                write(locale, childJson, childNode, isSaveAsStrings);
                if(childJson.size() > 0) {
                    json.add(key, childJson);
                }

            } else {
                TranslationValue translation = childNode.getValue();
                String content = translation.get(locale);
                if(content != null) {
                    if(Json5ArrayMapper.isArray(content)) {
                        json.add(key, Json5ArrayMapper.write(content));
                    } else if(StringUtil.isHexString(content)) {
                        json.add(key, Json5Primitive.of(content, true));
                    } else if(!isSaveAsStrings && NumberUtils.isCreatable(content)) {
                        json.add(key, Json5Primitive.of(NumberUtils.createNumber(content)));
                    } else {
                        json.add(key, Json5Primitive.of(StringEscapeUtils.unescapeJava(content)));
                    }
                }
            }
        }
    }
}
