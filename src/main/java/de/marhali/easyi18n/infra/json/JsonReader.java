package de.marhali.easyi18n.infra.json;

import com.google.gson.*;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
        switch (element) {
            case JsonObject object -> readObject(object, producer);
            case JsonArray array -> readArray(array, producer);
            case JsonPrimitive primitive -> readPrimitive(primitive, producer);
            case JsonNull nullValue -> readNull(nullValue, producer);
            default -> throw new UnsupportedOperationException("Unsupported JsonElement with class: " + element.getClass().getSimpleName());
        }
    }

    private void readObject(@NotNull JsonObject object, @NotNull TranslationProducer producer) {
        var levelledFileTemplate = templates.file().getAtLeveL(producer.level());

        for (String memberName : object.keySet()) {
            I18nParams memberNameParams = levelledFileTemplate.fromCanonical(memberName);
            var value = object.get(memberName);
            var childProducer = producer.withChildren(
                builder -> builder.mergeAll(memberNameParams).build(),
                level -> level + 1
            );
            read(value, childProducer);
        }
    }

    private void readArray(@NotNull JsonArray array, @NotNull TranslationProducer producer) {
        var arrayElements = new ArrayList<I18nValue.Primitive>();

        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) {
                // We only focus on primitives inside an array for now
                throw new UnsupportedOperationException("A JsonArray element may only consist of primitive elements");
            }

            arrayElements.add(readPrimitiveValue(element.getAsJsonPrimitive()));
        }

        var value = I18nValue.fromArray(arrayElements.toArray(new I18nValue.Primitive[0]));
        finallyProduceWithValue(producer, value);
    }

    private void readPrimitive(@NotNull JsonPrimitive primitive, @NotNull TranslationProducer producer) {
        finallyProduceWithValue(producer, readPrimitiveValue(primitive));
    }

    private @NotNull I18nValue.Primitive readPrimitiveValue(@NotNull JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return I18nValue.fromBarePrimitive(String.valueOf(primitive.getAsBoolean()));
        } else if (primitive.isString()) {
            return I18nValue.fromQuotedPrimitive(primitive.getAsString());
        } else if (primitive.isNumber()) {
            return I18nValue.fromBarePrimitive(String.valueOf(primitive.getAsNumber()));
        }

        throw new UnsupportedOperationException("Unsupported JsonPrimitive with class: " + primitive.getClass().getSimpleName());
    }

    private void readNull(@NotNull JsonNull ignoredNull, @NotNull TranslationProducer producer) {
        finallyProduceWithValue(producer, I18nValue.fromBarePrimitive("null"));
    }
}
