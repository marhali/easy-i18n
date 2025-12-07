package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Container to hold parsed i18n parameter values.
 * TODO: maybe split into 2 parts: I18nParams (record) and I18nParamsBuilder
 *
 * @author marhali
 */
public class I18nParams implements Cloneable {

    private final @NotNull Map<String, List<String>> params;

    public I18nParams() {
        this(new HashMap<>());
    }

    private I18nParams(@NotNull Map<String, List<String>> params) {
        this.params = params;
    }

    public boolean has(@NotNull String parameterName) {
        return this.params.containsKey(parameterName);
    }

    public @Nullable List<String> get(@NotNull String parameterName) {
        return this.params.get(parameterName);
    }

    public @Nullable String getFirst(@NotNull String parameterName) {
        return has(parameterName) ? this.params.get(parameterName).getFirst() : null;
    }

    public Collection<String> getOrDefault(@NotNull String parameterName, @NotNull Collection<String> orDefault) {
        return params.containsKey(parameterName) ? params.get(parameterName) : orDefault;
    }

    public void add(@NotNull String parameterName, @NotNull List<String> parameterValues) {
        this.params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>())
            .addAll(parameterValues);
    }

    public void add(@NotNull String parameterName, @NotNull String... parameterValues) {
        this.params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>())
            .addAll(List.of(parameterValues));
    }

    public void add(@NotNull String parameterName, @NotNull String parameterValue) {
        this.params.computeIfAbsent(parameterName, (_key) -> new ArrayList<>())
            .add(parameterValue);
    }

    public @NotNull Set<String> names() {
        return this.params.keySet();
    }

    @Override
    public String toString() {
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

    public int size() {
        return this.params.size();
    }

    @Override
    public I18nParams clone() {
        Map<String, List<String>> clonedParams = new HashMap<>(this.params.size());

        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            clonedParams.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        return new I18nParams(clonedParams);
    }
}
