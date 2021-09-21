package de.marhali.easyi18n.util.array;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * Utility methods to read and write json arrays.
 * @author marhali
 */
public class JsonArrayUtil extends ArrayUtil {
    public static String read(JsonArray array) {
        return read(array.iterator(), JsonElement::getAsString);
    }

    public static JsonArray write(String concat) {
        JsonArray array = new JsonArray();
        write(concat, array::add);
        return array;
    }
}
