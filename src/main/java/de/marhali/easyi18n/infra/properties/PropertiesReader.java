package de.marhali.easyi18n.infra.properties;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties specific reader.
 *
 * @author marhali
 */
public final class PropertiesReader extends FileReader {
    PropertiesReader(@NotNull I18nPath path, @NotNull Templates templates, @NotNull MutableI18nModule store) {
        super(path, templates, store);
    }

    void read(@NotNull LinkedProperties properties) {
        read(properties, createRootProducer());
    }

    private void read(@NotNull LinkedProperties properties, @NotNull TranslationProducer producer) {
        var levelledFileTemplate = templates.file().getAtLevel(producer.level());

        for (Object memberName : properties.keySet()) {
            I18nParams memberNameParams = levelledFileTemplate.fromCanonical(String.valueOf(memberName));
            var value = properties.get(memberName);
            var childProducer = producer.withChildren(
                (builder) -> builder.mergeAll(memberNameParams).build(),
                (level) -> level +1
            );
            readValue(value, childProducer);
        }
    }

    private void readValue(@NotNull Object value, @NotNull TranslationProducer producer) {
        if (value instanceof String[] stringArray) {
            List<I18nValue.Primitive> arrayElements = new ArrayList<>();

            for (String stringElement : stringArray) {
                arrayElements.add(I18nValue.fromBarePrimitive(stringElement));
            }

            finallyProduceWithValue(producer, I18nValue.fromArray(arrayElements.toArray(new I18nValue.Primitive[0])));
        } else {
            finallyProduceWithValue(producer, I18nValue.fromBarePrimitive(String.valueOf(value)));
        }
    }
}
