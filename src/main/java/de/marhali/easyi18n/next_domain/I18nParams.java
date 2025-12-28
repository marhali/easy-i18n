package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Container holding i18n parameter(s). Every parameter holds a list of associated values.
 *
 * @param params Map of parameter names and associated values
 * @author marhali
 */
public record I18nParams(
    @NotNull Map<String, List<String>> params
) {

    public I18nParams() {
        this(new HashMap<>());
    }

    public I18nParams(@NotNull Map<String, List<String>> params) {
        this.params = cloneParamsMap(params);
    }

    /**
     * Construct params using the builder pattern.
     *
     * @return {@link I18nParamsBuilder}
     */
    public static I18nParamsBuilder builder() {
        return new I18nParamsBuilder();
    }

    private static Map<String, List<String>> cloneParamsMap(@NotNull Map<String, List<String>> params) {
        var clonedParams = new HashMap<String, List<String>>(params.size());

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            clonedParams.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return clonedParams;
    }

    public boolean has(@NotNull String parameterName) {
        return this.params.containsKey(parameterName);
    }

    public @NotNull List<String> getOrEmpty(@NotNull String parameterName) {
        return this.params.getOrDefault(parameterName, List.of());
    }

    public @Nullable List<String> get(@NotNull String parameterName) {
        return this.params.get(parameterName);
    }

    public @NotNull String getOrThrowSingleton(@NotNull String parameterName) {
        var values = this.params.get(parameterName);

        if (values == null || values.size() != 1) {
            throw new IllegalArgumentException("Invalid singleton value for parameter with name '" + parameterName + "'. Got: " + values);
        }

        return values.getFirst();
    }

    public @NotNull Set<String> names() {
        return this.params.keySet();
    }

    public int size() {
        return this.params.size();
    }

    public @NotNull I18nParamsBuilder toBuilder() {
        return new I18nParamsBuilder(cloneParamsMap(params));
    }

    @Override
    public @NotNull String toString() {
        return "I18nParams{" +
            "params=" + params +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        I18nParams that = (I18nParams) o;
        return Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(params);
    }
}
