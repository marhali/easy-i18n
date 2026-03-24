package de.marhali.easyi18n.infra.yaml;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * YAML specific reader.
 *
 * @author marhali
 */
public final class YamlReader extends FileReader {

    YamlReader(@NotNull I18nPath path, @NotNull Templates templates, @NotNull MutableI18nModule store) {
        super(path, templates, store);
    }

    void read(@NotNull Map<Object, @Nullable Object> map) {
        readMap(map, createRootProducer());
    }

    @SuppressWarnings("unchecked")
    private void readMap(@NotNull Map<@NotNull Object, @Nullable Object> map, @NotNull TranslationProducer producer) {
        var levelledFileTemplate = templates.file().getAtLevel(producer.level());

        for (Object memberNameObject : map.keySet()) {
            var memberName = String.valueOf(memberNameObject);
            I18nParams memberNameParams = levelledFileTemplate.fromCanonical(memberName);
            var value = map.get(memberName);
            var childProducer = producer.withChildren(
                builder -> builder.mergeAll(memberNameParams).build(),
                (level) -> level + 1
            );

            if (value instanceof Map) {
                readMap((Map<Object, Object>) value, childProducer);
            } else if (value instanceof List) {
                readArray((List<Object>) value, childProducer);
            } else {
                readPrimitive(value, childProducer);
            }
        }
    }

    private void readArray(@NotNull List<@Nullable Object> array, @NotNull TranslationProducer producer) {
        var arrayElements = new ArrayList<I18nValue.Primitive>();

        for (Object element : array) {
            if (element instanceof Map || element instanceof List) {
                // We only focus on primitives inside an array for now
                throw new UnsupportedOperationException("A YAML-List element may only consist of primitive elements");
            }

            arrayElements.add(readPrimitiveValue(element));
        }

        var value = I18nValue.fromArray(arrayElements.toArray(new I18nValue.Primitive[0]));
        finallyProduceWithValue(producer, value);
    }

    private void readPrimitive(@Nullable Object value, @NotNull TranslationProducer producer) {
        finallyProduceWithValue(producer, readPrimitiveValue(value));
    }

    private @NotNull I18nValue.Primitive readPrimitiveValue(@Nullable Object value) {
        if (value instanceof String str) {
            return I18nValue.fromQuotedPrimitive(str);
        } else {
            return I18nValue.fromBarePrimitive(String.valueOf(value));
        }
    }
}
