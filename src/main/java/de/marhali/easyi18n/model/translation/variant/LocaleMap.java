package de.marhali.easyi18n.model.translation.variant;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps locale type with specified value
 * @author marhali
 */
public class LocaleMap extends HashMap<String, String> {

    public LocaleMap() {}

    public LocaleMap(Map<? extends String, ? extends String> m) {
        super(m);
    }
}