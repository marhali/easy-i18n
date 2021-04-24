package de.marhali.easyi18n.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.marhali.easyi18n.model.LocalizedNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Json tree utilities for writing and reading {@link LocalizedNode}'s
 * @author marhali
 */
public class JsonUtil {

    /**
     * Creates a {@link JsonObject} based from an {@link LocalizedNode}
     * @param locale Current locale
     * @param parent Parent json. Can be an entire json document
     * @param node The node instance
     */
    public static void writeTree(String locale, JsonObject parent, LocalizedNode node) {
        if(node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            if(node.getValue().get(locale) != null) {
                parent.add(node.getKey(), new JsonPrimitive(node.getValue().get(locale)));
            }

        } else {
            for(LocalizedNode children : node.getChildren()) {
                if(children.isLeaf()) {
                    writeTree(locale, parent, children);
                } else {
                    JsonObject childrenJson = new JsonObject();
                    writeTree(locale, childrenJson, children);
                    if(childrenJson.size() > 0) {
                        parent.add(children.getKey(), childrenJson);
                    }
                }
            }
        }
    }

    /**
     * Reads a {@link JsonObject} and writes the tree into the provided {@link LocalizedNode}
     * @param locale Current locale
     * @param json Json to read
     * @param data Node. Can be a root node
     */
    public static void readTree(String locale, JsonObject json, LocalizedNode data) {
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();

            try {
                // Try to go one level deeper
                JsonObject childObject = entry.getValue().getAsJsonObject();

                LocalizedNode childrenNode = data.getChildren(key);

                if(childrenNode == null) {
                    childrenNode = new LocalizedNode(key, new ArrayList<>());
                    data.addChildren(childrenNode);
                }

                readTree(locale, childObject, childrenNode);

            } catch(IllegalStateException e) { // Reached end for this node
                LocalizedNode leafNode = data.getChildren(key);

                if(leafNode == null) {
                    leafNode = new LocalizedNode(key, new HashMap<>());
                    data.addChildren(leafNode);
                }

                Map<String, String> messages = leafNode.getValue();
                messages.put(locale, entry.getValue().getAsString());
                leafNode.setValue(messages);
            }
        }
    }
}