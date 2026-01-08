package de.marhali.easyi18n.next_io.file.json;

import com.google.gson.*;
import de.marhali.easyi18n.next_domain.I18nModuleStore;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;
import de.marhali.easyi18n.next_io.file.FileMapper;
import de.marhali.easyi18n.next_io.TranslationProducer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * @author marhali
 */
public class JsonReader extends FileMapper {

    protected JsonReader(@NotNull I18nModuleStore store, @NotNull ModuleTemplate template, @NotNull I18nFile file) {
       super(store, template, file);
    }

    protected void read(@NotNull JsonElement element) {
        read(element, createRootProducer());
    }

    protected void read(@NotNull JsonElement element, @NotNull TranslationProducer producer) {
        if (element.isJsonObject()) {
            readObject(element.getAsJsonObject(), producer);
        } else if (element.isJsonArray()) {
            readArray(element.getAsJsonArray(), producer);
        } else if (element.isJsonPrimitive()) {
            readPrimitive(element.getAsJsonPrimitive(), producer);
        } else if (element.isJsonNull()) {
            readNull(element.getAsJsonNull(), producer);
        } else {
            throw new UnsupportedOperationException("Unsupported JsonElement with class: " + element.getClass().getSimpleName());
        }
    }

    protected void readObject(@NotNull JsonObject object, @NotNull TranslationProducer producer) {
        var fileTemplateLevel = template.file().getAtLevel(producer.level());

        for (String key : object.keySet()) {
            var keyParams = fileTemplateLevel.parse(key);
            var value = object.get(key);
            var childProducer = producer.children(
                paramsBuilder -> paramsBuilder.mergeAll(keyParams),
                level -> level + 1
            );
            read(value, childProducer);
        }
    }

    protected void readArray(@NotNull JsonArray array, @NotNull TranslationProducer producer) {
        var values = new ArrayList<>();

        for (JsonElement element : array) {
            if (!element.isJsonPrimitive()) {
                // We only focus on primitives inside an array for now
                throw new UnsupportedOperationException("A JsonArray element may only consist of primitive types");
            }

            values.add(readPrimitiveValue(element.getAsJsonPrimitive()));
        }

        store.getOrCreateTranslation(producer.toKey(template)).put(producer.locale(), values.toArray());
    }

    protected void readPrimitive(@NotNull JsonPrimitive primitive, @NotNull TranslationProducer producer) {
        var key = producer.toKey(template);
        var value = readPrimitiveValue(primitive);
        store.getOrCreateTranslation(key).put(producer.locale(), value);
    }

    private Object readPrimitiveValue(@NotNull JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        } else if (primitive.isString()) {
            return primitive.getAsString();
        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();
        }

        throw new UnsupportedOperationException("Unsupported JsonPrimitive with class: " + primitive.getClass().getSimpleName());
    }

    protected void readNull(@NotNull JsonNull ignoredNullable, @NotNull TranslationProducer producer) {
        var key = producer.toKey(template);
        store.getOrCreateTranslation(key).put(producer.locale(), null);
    }
}
