package de.marhali.easyi18n.ionext.parser.properties;

import de.marhali.easyi18n.ionext.parser.ArrayMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Map for 'properties' array values.
 * @author marhali
 */
public class PropertiesArrayMapper extends ArrayMapper {
    public static String read(String[] list) {
        return read(Arrays.stream(list).iterator(), Object::toString);
    }

    public static String[] write(String concat) {
        List<String> list = new ArrayList<>();
        write(concat, list::add);
        return list.toArray(new String[0]);
    }
}