package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.model.I18nParamsBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Variants formulator for a specific template.
 *
 * @param template Parsed template
 *
 * @author marhali
 */
public record TemplateValueFormulator(
    @NotNull Template template
) {
    public @NotNull Set<@NotNull TemplateValue> buildVariants(@NotNull I18nParams params) {
        String[][] values = buildValues(params);

        int resultSize = 1;
        for (String[] value : values) {
            resultSize = resultSize * value.length;
        }

        int valuesSize = values.length;
        int[] valueIndexes = new int[valuesSize];

        var result = new HashSet<@NotNull TemplateValue>(resultSize);

        for (int resultIndex = 0; resultIndex < resultSize; resultIndex++) {
            StringBuilder valueBuilder = new StringBuilder();
            I18nParamsBuilder paramsBuilder = I18nParams.builder();

            for (int valueIndex = 0; valueIndex < valuesSize; valueIndex++) {
                var value = values[valueIndex][valueIndexes[valueIndex]];

                valueBuilder.append(value);

                if (template.elements().get(valueIndex) instanceof TemplateElement.Placeholder placeholder) {
                    if (placeholder.hasDelimiter()) {
                        paramsBuilder.add(placeholder.name(), placeholder.splitByDelimiter(value));
                    } else {
                        paramsBuilder.add(placeholder.name(), value);
                    }
                }
            }

            result.add(new TemplateValue(valueBuilder.toString(), paramsBuilder.build()));
            incrementValueIndexes(valueIndexes, values);
        }

        return result;
    }

    private void incrementValueIndexes(int[] valueIndexes, @NotNull String[][] values) {
        for (int i = valueIndexes.length - 1; i >= 0; i--) {
            valueIndexes[i]++;

            if (valueIndexes[i] < values[i].length) {
                return;
            }

            valueIndexes[i] = 0;
        }
    }

    private @NotNull String[][] buildValues(@NotNull I18nParams params) {
        String[][] values = new String[template.elements().size()][];

        for (int i = 0; i < template.elements().size(); i++) {
            var element = template.elements().get(i);

            switch (element) {
                case TemplateElement.Literal literal -> values[i] = new String[]{literal.text()};
                case TemplateElement.Placeholder placeholder -> {
                    var paramName = placeholder.name();
                    var paramValues = params.get(paramName);

                    if (paramValues == null || paramValues.isEmpty()) {
                        throw new NoSuchElementException("Missing values for placeholder with name '" + paramName + "'");
                    }

                    values[i] = placeholder.hasDelimiter()
                        ? new String[] { placeholder.joinByDelimiter(paramValues) }
                        : paramValues.toArray(new String[0]);
                }
                default -> throw new IllegalArgumentException("Unknown template element: " + element.getClass().getSimpleName());
            }
        }

        return values;
    }
}
