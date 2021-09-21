package de.marhali.easyi18n.util.array;

import thito.nodeflow.config.ListSection;

/**
 * Utility methods to read and write yaml lists.
 * @author marhali
 */
public class YamlArrayUtil extends ArrayUtil {

    public static String read(ListSection list) {
       return read(list.iterator(), Object::toString);
    }

    public static ListSection write(String concat) {
        ListSection list = new ListSection();
        write(concat, list::add);
        return list;
    }
}