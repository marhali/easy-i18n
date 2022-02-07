package de.marhali.easyi18n.io.parser.json;

import com.google.gson.*;

import de.marhali.easyi18n.io.parser.ArrayMapper;

/**
 * Map json array values.
 * @author marhali
 */
public class JsonArrayMapper extends ArrayMapper {

    private static final Gson GSON = new GsonBuilder().create();

    public static String read(JsonArray array) {
        return read(array.iterator(), (jsonElement -> jsonElement.isJsonArray() || jsonElement.isJsonObject()
                ? "\\" + jsonElement
                : jsonElement.getAsString()));
    }

    public static JsonArray write(String concat) {
        JsonArray array = new JsonArray();

        write(concat, (element) -> {
            if(element.startsWith("\\")) {
                array.add(GSON.fromJson(element.replace("\\", ""), JsonElement.class));
            } else {
                array.add(element);
            }
        });

        return array;
    }
}