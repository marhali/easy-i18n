package de.marhali.easyi18n.infra.json;

import com.google.gson.*;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import org.jetbrains.annotations.NotNull;

/**
 * JSON specific reader.
 * Responsible for converting {@link JsonElement}'s to {@link TranslationProducer}'s.
 *
 * @author marhali
 */
public final class JsonReader extends FileReader {

    JsonReader(@NotNull I18nPath path, @NotNull Templates templates, @NotNull MutableI18nModule store) {
        super(path, templates, store);
    }

    void read(@NotNull JsonElement element) {
        read(element, createRootProducer());
    }

    private void read(@NotNull JsonElement element, @NotNull TranslationProducer producer) {
        if (element.isJsonObject()) {
            readObject(element.getAsJsonObject(), producer);
        } else {
            readValue(element, producer);
        }
    }

    private void readObject(@NotNull JsonObject object, @NotNull TranslationProducer producer) {
        var levelledFileTemplate = templates.file().getAtLevel(producer.level());

        for (String memberName : object.keySet()) {
            I18nParams memberNameParams = levelledFileTemplate.fromCanonical(memberName);
            var value = object.get(memberName);
            var childProducer = producer.withChildren(
                (builder) -> builder.mergeAll(memberNameParams).build(),
                (level) -> level + 1
            );
            read(value, childProducer);
        }
    }

    private void readValue(@NotNull JsonElement element, @NotNull TranslationProducer producer) {
        String dumpedElement = element.toString(); // JsonElement#toString() already provides escaped characters
        finallyProduceWithValue(producer, new I18nValue(dumpedElement));
    }
}
