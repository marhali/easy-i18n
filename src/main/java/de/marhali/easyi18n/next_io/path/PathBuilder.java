package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @author marhali
 */
public record PathBuilder(@NotNull List<TemplateSegment> segments) {
    public @NotNull Set<String> build(@NotNull I18nParams params) {
        String[][] elements = buildElements(params);

        int pathsSize = 1;
        for (String[] element : elements) {
            pathsSize = pathsSize * element.length;
        }

        int elementsSize = elements.length;
        int[] indexes = new int[elementsSize];

        var paths = new HashSet<String>(pathsSize);

        for (int pathIndex = 0; pathIndex < pathsSize; pathIndex++) {
            StringBuilder builder = new StringBuilder();

            for (int elementIndex = 0; elementIndex < elementsSize; elementIndex++) {
                builder.append(elements[elementIndex][indexes[elementIndex]]);
            }
            paths.add(builder.toString());
            incrementIndex(indexes, elements);
        }

        return paths;
    }

    private void incrementIndex(int[] indexes, String[][] elements) {
        for (int i = indexes.length - 1; i >= 0; i--) {
            indexes[i]++;
            if (indexes[i] < elements[i].length) {
                return;
            }
            indexes[i] = 0;
        }
    }

    private @NotNull String[][] buildElements(@NotNull I18nParams params) {
        String[][] elements = new String[segments.size()][];

        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);

            if (segment.isLiteral()) {
                elements[i] = new String[] { segment.getAsLiteral().getLiteral() };
            } else if (segment.isParameter()) {
                var name = segment.getAsParameter().getName();
                var values = params.get(name);

                if (values == null || values.isEmpty()) {
                    throw new NoSuchElementException("Missing values for parameter with name '"  + name + "'");
                }

                // TODO: we might also validate each value against the parameter constraint

                elements[i] = values.toArray(new String[0]);
            } else {
                throw new IllegalArgumentException("Unsupported template segment with type: " + segment.getClass().getSimpleName());
            }
        }

        return elements;
    }

    @Override
    public @NotNull String toString() {
        return "PathBuilder{" +
            "segments=" + segments +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PathBuilder that = (PathBuilder) o;
        return Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(segments);
    }
}
