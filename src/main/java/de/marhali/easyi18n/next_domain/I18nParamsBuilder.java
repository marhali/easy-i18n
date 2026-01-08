package de.marhali.easyi18n.next_domain;

import de.marhali.easyi18n.next_io.I18nBuiltinParam;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Construct i18n parameter(s) using the builder pattern.
 *
 * @author marhali
 */
public class I18nParamsBuilder {

    private final @NotNull Map<@NotNull String, @NotNull List<@NotNull String>> params;

    protected I18nParamsBuilder() {
        this(new HashMap<>());
    }

    protected I18nParamsBuilder(@NotNull Map<String, List<String>> params) {
        this.params = params;
    }

    public @NotNull I18nParamsBuilder addAll(@NotNull I18nParams params) {
        this.params.putAll(params.params());
        return this;
    }

    public @NotNull I18nParamsBuilder mergeAll(@NotNull I18nParams params) {
        for (Map.Entry<String, List<String>> entry : params.params().entrySet()) {
            add(entry.getKey(), entry.getValue());
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
        return add(parameter.getParamName(), Arrays.asList(parameterValues));
    }

    public @NotNull I18nParamsBuilder add(@NotNull String parameterName, @NotNull List<String> parameterValues) {
        params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>()).addAll(parameterValues);
        return this;
    }

    public @NotNull I18nParams build() {
        return new I18nParams(params);
    }
}
