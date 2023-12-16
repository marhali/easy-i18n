package de.marhali.easyi18n.io.parser.json5;

import de.marhali.easyi18n.io.parser.ArrayMapper;
import de.marhali.easyi18n.util.StringUtil;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Array;
import de.marhali.json5.Json5Primitive;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * Map json5 array values.
 * @author marhali
 */
public class Json5ArrayMapper extends ArrayMapper {

    private static final Json5 JSON5 = Json5.builder(builder ->
            builder.allowInvalidSurrogate().quoteSingle().indentFactor(0).build());

    public static String read(Json5Array array) {
        return read(array.iterator(), (jsonElement -> {
            try {
                return jsonElement.isJson5Array() || jsonElement.isJson5Object()
                        ? "\\" + JSON5.serialize(jsonElement)
                        : jsonElement.getAsString();
            } catch (IOException e) {
                throw new AssertionError(e.getMessage(), e.getCause());
            }
        }));
    }

    public static Json5Array write(String concat) {
        Json5Array array = new Json5Array();

        write(concat, (element) -> {
            if(element.startsWith("\\")) {
                array.add(JSON5.parse(element.replace("\\", "")));
            } else {
                if(StringUtil.isHexString(element)) {
                    array.add(Json5Primitive.of(element, true));
                } else if(NumberUtils.isCreatable(element)) {
                    array.add(Json5Primitive.of(NumberUtils.createNumber(element)));
                } else {
                    array.add(Json5Primitive.of(element));
                }
            }
        });

        return array;
    }
}
