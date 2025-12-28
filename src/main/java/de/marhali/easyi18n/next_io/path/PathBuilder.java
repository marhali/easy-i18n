package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nParamsBuilder;
import de.marhali.easyi18n.next_io.I18nPath;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import de.marhali.easyi18n.next_io.template.TemplateValue;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @param segments Path template {@link TemplateSegment segments}
 * @author marhali
 */
public record PathBuilder(
    @NotNull List<TemplateSegment> segments
) {
    /**
     * Builds all {@link I18nPath paths} using the provided {@link I18nParams params}.
     * @param params {@link I18nParams}
     * @return Set of {@link I18nPath paths}
     */
    public @NotNull Set<I18nPath> build(@NotNull I18nParams params) {
        TemplateValue[][] values = buildValues(params);

        int pathsCount = 1;
        for (TemplateValue[] value : values) {
            pathsCount = pathsCount * value.length;
        }

        int valuesCount = values.length;
        int[] valueIndexes = new int[valuesCount];

        var paths = new HashSet<I18nPath>(pathsCount);

        for (int pathIndex = 0; pathIndex < pathsCount; pathIndex++) {
            StringBuilder pathBuilder = new StringBuilder();
            I18nParamsBuilder pathParams = I18nParams.builder();

            for (int valueIndex = 0; valueIndex < valuesCount; valueIndex++) {
                var value = values[valueIndex][valueIndexes[valueIndex]];

                if (value.isLiteral()) {
                    pathBuilder.append(value.getAsLiteral().getText());
                } else if (value.isParameter()) {
                    var parameter = value.getAsParameter();
                    pathBuilder.append(parameter.getValue());
                    pathParams.add(parameter.getName(), parameter.getValue());
                } else {
                    throw new IllegalArgumentException("Unsupported template value with class: " + value.getClass().getSimpleName());
                }
            }

            paths.add(new I18nPath(pathBuilder.toString(), pathParams.build()));
            incrementValueIndexes(valueIndexes, values);
        }

        return paths;
    }

    private void incrementValueIndexes(int[] valueIndexes, TemplateValue[][] values) {
        for (int i = valueIndexes.length - 1; i >= 0; i--) {
            valueIndexes[i]++;

            if (valueIndexes[i] < values[i].length) {
                return;
            }

            valueIndexes[i] = 0;
        }
    }

    private @NotNull TemplateValue[][] buildValues(@NotNull I18nParams params) {
        TemplateValue[][] values = new TemplateValue[segments.size()][];

        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);

            if (segment.isLiteral()) {
                values[i] = new TemplateValue[]{TemplateValue.fromLiteral(segment.getAsLiteral().getText())};
            } else if (segment.isParameter()) {
                var parameterName = segment.getAsParameter().getName();
                var parameterValues = params.get(parameterName);

                if (parameterValues == null || parameterValues.isEmpty()) {
                    throw new NoSuchElementException("Missing value(s) for parameter with name '" + parameterName + "'");
                }

                // TODO: we might also validate each value against the parameter constraint

                values[i] = parameterValues.stream()
                    .map((parameterValue) -> TemplateValue.fromParameter(parameterName, parameterValue))
                    .toArray(TemplateValue[]::new);
            } else {
                throw new IllegalArgumentException("Unsupported template segment with class: " + segment.getClass().getSimpleName());
            }
        }

        return values;
    }
}
