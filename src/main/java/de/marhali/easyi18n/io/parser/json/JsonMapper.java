package de.marhali.easyi18n.io.parser.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Map;

/**
 * Mapper for mapping json objects into translation nodes and backwards.
 * @author marhali
 */
public class JsonMapper {

    public static void read(String locale, JsonObject json, TranslationNode node) {
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            TranslationNode childNode = node.getOrCreateChildren(key);

            if(value.isJsonObject()) {
                // Nested element - run recursively
                read(locale, value.getAsJsonObject(), childNode);
            } else {
                TranslationValue translation = childNode.getValue();

                String content = entry.getValue().isJsonArray()
                        ? JsonArrayMapper.read(value.getAsJsonArray())
                        : StringUtil.escapeControls(value.getAsString(), true);

                translation.put(locale, content);
                childNode.setValue(translation);
            }
        }
    }

    public static void write(String locale, JsonObject json, TranslationNode node, boolean isSaveAsString) {
        for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String key = entry.getKey();
            TranslationNode childNode = entry.getValue();

            if(!childNode.isLeaf()) {
                // Nested node - run recursively
                JsonObject childJson = new JsonObject();
                write(locale, childJson, childNode, isSaveAsString);
                if(childJson.size() > 0) {
                    json.add(key, childJson);
                }
            } else {
                TranslationValue translation = childNode.getValue();
                String content = translation.get(locale);

                if(content != null) {
                    if(JsonArrayMapper.isArray(content)) {
                        json.add(key, JsonArrayMapper.write(content));
                    } else if(!isSaveAsString && NumberUtils.isCreatable(content)) {
                        json.add(key, new JsonPrimitive(NumberUtils.createNumber(content)));
                    } else {
                        json.add(key, new JsonPrimitive(StringEscapeUtils.unescapeJava(content)));
                    }
                }
            }
        }
    }
}