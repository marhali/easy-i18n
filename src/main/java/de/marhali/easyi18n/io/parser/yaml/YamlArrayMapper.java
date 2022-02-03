package de.marhali.easyi18n.io.parser.yaml;

import de.marhali.easyi18n.io.parser.ArrayMapper;

import thito.nodeflow.config.ListSection;

/**
 * Map for yaml array values.
 * @author marhali
 */
public class YamlArrayMapper extends ArrayMapper {
    public static String read(ListSection list) {
        return read(list.iterator(), Object::toString);
    }

    public static ListSection write(String concat) {
        ListSection list = new ListSection();
        write(concat, list::add);
        return list;
    }
}