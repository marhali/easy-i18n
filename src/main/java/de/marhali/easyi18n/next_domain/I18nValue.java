package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all translation values (<code>locale -> value</code>) for a specific {@link I18nKey}.
 *
 * @author marhali
 */
public class I18nValue {

    /**
     * Map including every managed translation value by locale key.
     */
    @NotNull
    private final Map<String, Object> byLocale;

    // TODO: evaluate if we want to do comments now
    @Nullable
    private String comment;

    protected I18nValue() {
        this(new HashMap<>());
    }

    protected I18nValue(@NotNull Map<String, Object> byLocale) {
        this.byLocale = byLocale;
    }

    public void put(@NotNull String locale, Object value) {
        this.byLocale.put(locale, value);
    }

    @Override
    public String toString() {
        return "I18nValue{" +
            "byLocale=" + byLocale +
            ", comment='" + comment + '\'' +
            '}';
    }
}
