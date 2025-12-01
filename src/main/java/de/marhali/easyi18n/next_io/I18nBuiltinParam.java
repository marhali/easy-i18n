package de.marhali.easyi18n.next_io;

import java.util.Arrays;

/**
 * Collection of builtin i18n parameters.
 * These parameters are needed to build the core logic.
 * The user is free to define other parameters.
 *
 * @author marhali
 */
public enum I18nBuiltinParam {
    // TODO: do we need module?
    MODULE("module"),
    LOCALE("locale"),
    ;

    public static I18nBuiltinParam fromName(String paramName) {
        return Arrays.stream(I18nBuiltinParam.values())
            .filter(param -> param.paramName.equals(paramName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown i18n parameter with name: " + paramName));
    }

    private final String paramName;

    I18nBuiltinParam(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
