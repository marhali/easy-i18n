package de.marhali.easyi18n.infra.yaml;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            } else {
                readValue(value, childProducer);
            }
        }
    }

    private void readValue(@Nullable Object value, @NotNull TranslationProducer producer) {
        String dumpedValue = YamlFileProcessor.YAML_MINIFY.dump(value);
        dumpedValue = dumpedValue.replaceFirst("\\n$", ""); // Replace unnecessary last \n
        finallyProduceWithValue(producer, I18nValue.fromUnescaped(dumpedValue));
    }
}
