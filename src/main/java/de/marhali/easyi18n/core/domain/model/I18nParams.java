package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Container holding i18n parameter(s) with multi-value support.
 *
 * @param params I18n parameters
 *
 * @author marhali
 */
public record I18nParams(
    @NotNull Map<@NotNull String, @NotNull List<@NotNull String>> params
    ) {

    /**
     * Construct params using the builder pattern.
     *
     * @return {@link I18nParamsBuilder}
     */
    public static I18nParamsBuilder builder() {
        return new I18nParamsBuilder();
    }

    /**
     * Checks whether a parameter has values or not.
     * @param parameterName The parameter name
     * @return {@code true} if any values exist, otherwise {@code false}
     */
    public boolean has(@NotNull String parameterName) {
        return params.containsKey(parameterName);
    }

    /**
     * Retrieves values for a specific parameter.
     * @param parameterName The parameter name
     * @return Nullable list of parameter values
     */
    public @Nullable List<@NotNull String> get(@NotNull String parameterName) {
        return params.get(parameterName);
    }

    /**
     * Retrieves values for a specific parameter with fallback to empty list.
     * @param parameterName The parameter name
     * @return Parameter values or empty list as if parameter is unknown
     */
    public @NotNull List<@NotNull String> getOrEmpty(@NotNull String parameterName) {
        return params.getOrDefault(parameterName, List.of());
    }

    /**
     * Checks whether any parameters are defined.
     * @return {@code true} if no params are set, otherwise {@code false}
     */
    public boolean isEmpty() {
        return params.isEmpty();
    }

    /**
     * Transforms this record to a mutable params builder.
     * @return {@link I18nParamsBuilder}
     */
    public @NotNull I18nParamsBuilder toBuilder() {
        return new I18nParamsBuilder(cloneParamsMap(params));
    }

    /**
     * Internal function to deeply clone the params map.
     * @param params Map to clone
     * @return Cloned params map
     */
    private static @NotNull Map<@NotNull String, @NotNull List<@NotNull String>> cloneParamsMap(
        @NotNull Map<@NotNull String, @NotNull List<@NotNull String>> params
    ) {
        var clonedParams = new HashMap<@NotNull String, @NotNull List<@NotNull String>>(params.size());

        for (Map.Entry<@NotNull String, @NotNull List<@NotNull String>> entry : params.entrySet()) {
            clonedParams.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return clonedParams;
    }
}
