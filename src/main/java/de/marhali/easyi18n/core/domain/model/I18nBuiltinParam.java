package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Enumeration of builtin i18n parameters.
 * These parameters are needed to build the core logic.
 * The user is free to define other parameters.
 *
 * @author marhali
 */
public enum I18nBuiltinParam {
    LOCALE("locale"),
    ;

    /**
     * Resolves enum entry the by the parameter name
     * @param parameterName The parameter name
     * @return {@link I18nBuiltinParam}
     */
    public static @NotNull I18nBuiltinParam fromParameterName(@NotNull String parameterName) {
        return Arrays.stream(I18nBuiltinParam.values())
            .filter(param -> param.parameterName.equals(parameterName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown i18n parameter with name: " + parameterName));
    }

    /**
     * Parameter name that can be used in templates.
     */
    private final @NotNull String parameterName;

    I18nBuiltinParam(@NotNull String parameterName) {
        this.parameterName = parameterName;
    }

    public @NotNull String getParameterName() {
        return parameterName;
    }
}
