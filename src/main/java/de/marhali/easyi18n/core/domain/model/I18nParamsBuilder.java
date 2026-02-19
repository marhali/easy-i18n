package de.marhali.easyi18n.core.domain.model;

import de.marhali.easyi18n.next_io.I18nBuiltinParam;
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
        return add(parameter.getParamName(), Arrays.asList(parameterValues));
    }

    public @NotNull I18nParamsBuilder add(@NotNull String parameterName, @NotNull List<String> parameterValues) {
        params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>()).addAll(parameterValues);
        return this;
    }

    public @NotNull I18nParamsBuilder remove(@NotNull String parameterName) {
        params.remove(parameterName);
        return this;
    }

    public @NotNull I18nParamsBuilder removeAllExcept(@NotNull Set<String> allowedParameterNames) {
        var paramsToRemove = new HashSet<>(params.keySet());
        paramsToRemove.removeAll(allowedParameterNames);
        paramsToRemove.forEach(this::remove);
        return this;
    }

    public @NotNull I18nParams build() {
        return new I18nParams(params);
    }
}
