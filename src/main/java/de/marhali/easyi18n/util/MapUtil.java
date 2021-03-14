package de.marhali.easyi18n.util;

import de.marhali.easyi18n.model.LocalizedNode;

import java.util.List;
import java.util.TreeMap;

/**
 * Map utilities.
 * @author marhali
 */
public class MapUtil {

    /**
     * Converts the provided list into a tree map.
     * @param list List of nodes
     * @return TreeMap based on node key and node object
     */
    public static TreeMap<String, LocalizedNode> convertToTreeMap(List<LocalizedNode> list) {
        TreeMap<String, LocalizedNode> map = new TreeMap<>();

        for(LocalizedNode item : list) {
            map.put(item.getKey(), item);
        }

        return map;
    }
}