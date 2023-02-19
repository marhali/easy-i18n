package de.marhali.easyi18n.io.parser.yaml;

import de.marhali.easyi18n.io.parser.ArrayMapper;

import java.util.ArrayList;
import java.util.List;

public class YamlArrayMapper extends ArrayMapper {
    public static String read(List<Object> list) {
        return read(list.iterator(), Object::toString);
    }

    public static List<Object> write(String concat) {
        List<Object> list = new ArrayList<>();
        write(concat, list::add);
        return list;
    }
}
