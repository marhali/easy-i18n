package de.marhali.easyi18n.core.domain.model;

import de.marhali.easyi18n.core.domain.template.TemplateElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Similar to {@link I18nParams} but tracks consumed parameters with an index.
 *
 * @param params I18n params
 * @param indexAtParam Tracked index at each parameter
 *
 * @see I18nParams
 * @author marhali
 */
public record IndexedI18nParams(
    @NotNull I18nParams params,
    @NotNull Map<@NotNull String, @NotNull Integer> indexAtParam
    ) {

    /**
     * Retrieves the current index for the given parameter name.
     * @param parameterName Parameter name
     * @return tracked index for given parameter or {@code 0} as fallback
     */
    public int getIndexForParameter(@NotNull String parameterName) {
        return indexAtParam.getOrDefault(parameterName, 0);
    }

    /**
     * Retrieves all parameter values for the given parameter name.
     * @param parameterName Parameter name
     * @return List of all mapped values or throws {@link NullPointerException} if parameter name is unknown
     */
    public @NotNull List<@NotNull String> getAllParameterValues(@NotNull String parameterName) {
        return Objects.requireNonNull(params.get(parameterName), "Parameter '" + parameterName + "' has no values");
    }

    /**
     * Resolves the parameter value at the current index for the given parameter name.
     * @param parameterName Parameter name
     * @return Parameter value
     */
    public @NotNull String getParameterValueAtIndex(@NotNull String parameterName) {
        int index = getIndexForParameter(parameterName);
        var values = params.get(parameterName);

        if (values == null || index >= values.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for parameter '" + parameterName + "' (" + values + ")");
        }

        return values.get(index);
    }

    /**
     * Checks whether all params have been fully consumed or not.
     * @return {@code true} if all parameters are consumed (index = params size), otherwise {@code false}
     */
    public boolean isFullyIndexed() {
        return params.params().entrySet().stream()
            .allMatch((entry) -> entry.getValue().size() == getIndexForParameter(entry.getKey()));
    }

    /**
     * Increments the indexes for the given parameter names.
     * @param parametersToIncrement Parameters to increase the index
     * @return {@link IndexedI18nParams}
     */
    public @NotNull IndexedI18nParams withIncrementParameters(
        @NotNull Set<TemplateElement.@NotNull Placeholder> parametersToIncrement
    ) {
        var indexAtParamCopy = new HashMap<>(indexAtParam);

        for (TemplateElement.Placeholder placeholder : parametersToIncrement) {
            indexAtParamCopy.compute(placeholder.name(), (_parameterName, previousIndex) -> {
                if (placeholder.hasDelimiter()) {
                    return params.getOrEmpty(placeholder.name()).size();
                } else {
                    return previousIndex == null ? 1: previousIndex + 1;
                }
            });
        }

        return new IndexedI18nParams(params, indexAtParamCopy);
    }
}
