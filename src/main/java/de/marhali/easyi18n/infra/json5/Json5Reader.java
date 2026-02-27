package de.marhali.easyi18n.infra.json5;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import de.marhali.json5.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * JSON5 specific reader.
 *
 * @author marhali
 */
public final class Json5Reader extends FileReader {

    Json5Reader(@NotNull I18nPath path, @NotNull Templates templates, @NotNull MutableI18nModule store) {
        super(path, templates, store);
    }

    void read(@NotNull Json5Element element) {
        read(element, createRootProducer());
    }

    private void read(@NotNull Json5Element element, @NotNull TranslationProducer producer) {
        switch (element) {
            case Json5Object object -> readObject(object, producer);
            case Json5Array array -> readArray(array, producer);
            case Json5Primitive primitive -> readPrimitive(primitive, producer);
            case Json5Null nullValue -> readNull(nullValue, producer);
            default -> throw new UnsupportedOperationException("Unsupported Json5Element with class: " + element.getClass().getSimpleName());
        }
    }

    private void readObject(@NotNull Json5Object object, @NotNull TranslationProducer producer) {
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

    private void readArray(@NotNull Json5Array array, @NotNull TranslationProducer producer) {
        var arrayElements = new ArrayList<I18nValue.Primitive>();

        for (Json5Element element : array) {
            if (!element.isJson5Primitive()) {
                // We only focus on primitives inside an array for now
                throw new UnsupportedOperationException("A Json5Array element may only consist of primitive elements");
            }

            arrayElements.add(readPrimitiveValue(element.getAsJson5Primitive()));
        }

        var value = I18nValue.fromArray(arrayElements.toArray(new I18nValue.Primitive[0]));
        finallyProduceWithValue(producer, value);
    }

    private void readPrimitive(@NotNull Json5Primitive primitive, @NotNull TranslationProducer producer) {
        finallyProduceWithValue(producer, readPrimitiveValue(primitive));
    }

    private @NotNull I18nValue.Primitive readPrimitiveValue(@NotNull Json5Primitive primitive) {
        if (primitive.isBoolean()) {
            return I18nValue.fromBarePrimitive(String.valueOf(primitive.getAsBoolean()));
        } else if (primitive.isString()) {
            return I18nValue.fromQuotedPrimitive(primitive.getAsString());
        } else if (primitive.isNumber()) {
            return I18nValue.fromBarePrimitive(String.valueOf(primitive.getAsNumber()));
        } else if (primitive.isBinaryNumber()) {
            return I18nValue.fromBarePrimitive(primitive.getAsBinaryString());
        } else if (primitive.isOctalNumber()) {
            return I18nValue.fromBarePrimitive(primitive.getAsOctalString());
        } else if (primitive.isHexNumber()) {
            return I18nValue.fromBarePrimitive(primitive.getAsHexString());
        } else if (primitive.isInstant()) {
            return I18nValue.fromBarePrimitive(primitive.getAsInstant().toString());
        }

        throw new UnsupportedOperationException("Unsupported JsonPrimitive: " + primitive);
    }

    private void readNull(@NotNull Json5Null ignoredNull, @NotNull TranslationProducer producer) {
        finallyProduceWithValue(producer, I18nValue.fromBarePrimitive("null"));
    }
}
