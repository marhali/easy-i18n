package de.marhali.easyi18n.infra.json;

import com.google.gson.*;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.model.TranslationTarget;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileWriter;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

/**
 * JSON specific writer.
 * Responsible for converting {@link TranslationConsumer}'s to the target {@link JsonElement}'s.
 *
 * @author marhali
 */
public final class JsonWriter extends FileWriter {

    private final @NotNull JsonObject rootElement;

    JsonWriter(@NotNull I18nPath path, @NotNull Templates templates) {
        super(path, templates);
        this.rootElement = new JsonObject();
    }

    void write(@NotNull Set<@NotNull TranslationConsumer> translations) {
        for (TranslationTarget target : mapConsumersToSortedTargets(translations)) {
            write(target);
        }
    }

    private void write(@NotNull TranslationTarget target) {
        Iterator<String> hierarchyIterator = target.canonicalHierarchy().iterator();

        JsonObject targetObject = rootElement;
        String memberName = null;

        while (hierarchyIterator.hasNext()) {
            memberName = hierarchyIterator.next();
            boolean hasChild = hierarchyIterator.hasNext();

            if (hasChild) {
                if (targetObject.has(memberName)) {
                    targetObject = targetObject.getAsJsonObject(memberName);
                } else {
                    var newTargetObject = new JsonObject();
                    targetObject.add(memberName, newTargetObject);
                    targetObject = newTargetObject;
                }
            }
        }

        if (memberName == null) {
            throw new IllegalStateException("Missing last hierarchical property for: " + target);
        }

        targetObject.add(memberName, toJsonElement(target.value()));
    }

    @NotNull JsonElement getRootElement() {
        return this.rootElement;
    }

    private @NotNull JsonElement toJsonElement(@NotNull I18nValue value) {
        return switch (value) {
            case I18nValue.Primitive primitive -> toJsonPrimitive(primitive);
            case I18nValue.Array array -> toJsonArray(array);
        };
    }

    private @NotNull JsonArray toJsonArray(@NotNull I18nValue.Array array) {
        JsonArray jsonArray = new JsonArray(array.elements().length);

        for (I18nValue.Primitive element : array.elements()) {
            jsonArray.add(toJsonPrimitive(element));
        }

        return jsonArray;
    }

    private @NotNull JsonElement toJsonPrimitive(@NotNull I18nValue.Primitive primitive) {
        return switch (primitive) {
            case I18nValue.Quoted quoted -> new JsonPrimitive(quoted.text());
            case I18nValue.Bare bare -> {
                JsonElement element;

                try {
                    element = JsonParser.parseString(bare.text());
                } catch (JsonSyntaxException ex) {
                    throw new RuntimeException(ex);
                }

                if (element == null || !(element.isJsonPrimitive() || element.isJsonNull())
                    || (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())) {
                    throw new IllegalArgumentException("Invalid bare value '" + bare.text() + "'. Must be Boolean, Number, Array, null or quoted String");
                }

                yield element;
            }
        };
    }
}
