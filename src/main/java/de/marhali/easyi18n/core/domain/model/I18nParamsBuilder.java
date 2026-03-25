package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Construct i18n {@link I18nParams parameters} using the builder pattern.
 *
 * @author marhali
 */
public class I18nParamsBuilder {

    private final @NotNull Map<@NotNull String, @NotNull List<@NotNull String>> params;

    protected I18nParamsBuilder() {
        this(new HashMap<>());
    }

    protected I18nParamsBuilder(@NotNull Map<@NotNull String, List<String>> params) {
        this.params = params;
    }

    public @NotNull I18nParamsBuilder addAll(@NotNull I18nParams params) {
        this.params.putAll(params.params());
        return this;
    }

    public @NotNull I18nParamsBuilder mergeAll(@NotNull I18nParams params) {
        for (Map.Entry<@NotNull String, List<String>> entry : params.params().entrySet()) {
            this.params.computeIfAbsent(entry.getKey(), (_key) -> new ArrayList<>()).addAll(entry.getValue());
        }
        return this;
    }

    public @NotNull I18nParamsBuilder add(@NotNull String parameterName, @NotNull String parameterValue) {
        params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>()).add(parameterValue);
        return this;
    }

    public @NotNull I18nParamsBuilder add(@NotNull String parameterName, @NotNull String... parameterValues) {
        return add(parameterName, Arrays.asList(parameterValues));
    }

    public @NotNull I18nParamsBuilder add(@NotNull I18nBuiltinParam parameter, @NotNull String... parameterValues) {
        return add(parameter.getParameterName(), Arrays.asList(parameterValues));
    }

    public @NotNull I18nParamsBuilder add(@NotNull String parameterName, @NotNull List<String> parameterValues) {
        params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>()).addAll(parameterValues);
        return this;
    }

    public @NotNull I18nParamsBuilder remove(@NotNull String parameterName) {
        params.remove(parameterName);
        return this;
    }

    public @NotNull I18nParamsBuilder put(@NotNull String parameterName, @NotNull List<@NotNull String> parameterValue) {
        params.put(parameterName, parameterValue);
        return this;
    }

    public @NotNull I18nParamsBuilder put(@NotNull I18nBuiltinParam parameter,  @NotNull List<@NotNull String> parameterValue) {
        return put(parameter.getParameterName(), parameterValue);
    }

    public @NotNull I18nParamsBuilder removeKeys(@NotNull Set<@NotNull String> keys) {
        for (String key : keys) {
            params.remove(key);
        }
        return this;
    }

    public @NotNull I18nParams build() {
        return new I18nParams(params);
    }
}
