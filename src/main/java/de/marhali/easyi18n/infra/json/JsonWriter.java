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
        try {
            return JsonParser.parseString(value.raw());
        } catch (JsonSyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
