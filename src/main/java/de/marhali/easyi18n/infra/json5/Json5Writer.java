package de.marhali.easyi18n.infra.json5;

import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.model.TranslationTarget;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileWriter;
import de.marhali.json5.*;
import de.marhali.json5.stream.Json5Lexer;
import de.marhali.json5.stream.Json5Parser;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

/**
 * JSON5 specific writer.
 *
 * @author marhali
 */
public final class Json5Writer extends FileWriter {

    private final @NotNull Json5Object rootElement;

    Json5Writer(@NotNull I18nPath path, @NotNull Templates templates) {
        super(path, templates);
        this.rootElement = new Json5Object();
    }

    void write(@NotNull Set<@NotNull TranslationConsumer> translations) {
        for (TranslationTarget target : mapConsumersToSortedTargets(translations)) {
            write(target);
        }
    }

    private void write(@NotNull TranslationTarget target) {
        Iterator<String> hierarchyIterator = target.canonicalHierarchy().iterator();

        Json5Object targetObject = rootElement;
        String memberName = null;

        while (hierarchyIterator.hasNext()) {
            memberName = hierarchyIterator.next();
            boolean hasChild = hierarchyIterator.hasNext();

            if (hasChild) {
                if (targetObject.has(memberName)) {
                    targetObject = targetObject.getAsJson5Object(memberName);
                } else {
                    var newTargetObject = new Json5Object();
                    targetObject.add(memberName, newTargetObject);
                    targetObject = newTargetObject;
                }
            }
        }

        if (memberName == null) {
            throw new IllegalStateException("Missing last hierarchical property for: " + target);
        }

        targetObject.add(memberName, toJson5Element(target.value()));
    }

    @NotNull Json5Element getRootElement() {
        return this.rootElement;
    }

    private @NotNull Json5Element toJson5Element(@NotNull I18nValue value) {
        return switch (value) {
            case I18nValue.Primitive primitive -> toJson5Primitive(primitive);
            case I18nValue.Array array -> toJson5Array(array);
        };
    }

    private @NotNull Json5Array toJson5Array(@NotNull I18nValue.Array array) {
        Json5Array jsonArray = new Json5Array(array.elements().length);

        for (I18nValue.Primitive element : array.elements()) {
            jsonArray.add(toJson5Primitive(element));
        }

        return jsonArray;
    }

    private @NotNull Json5Element toJson5Primitive(@NotNull I18nValue.Primitive primitive) {
        return switch (primitive) {
            case I18nValue.Quoted quoted -> Json5Primitive.fromString(quoted.text());
            case I18nValue.Bare bare -> {
                Json5Element element;

                try {
                    var reader = new StringReader(bare.text());
                    var lexer = new Json5Lexer(reader, Json5FileProcessor.JSON5_OPTIONS);
                    element = Json5Parser.parse(lexer);
                    reader.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

                if (element == null || !(element.isJson5Primitive() || element.isJson5Null())
                    || (element.isJson5Primitive() && element.getAsJson5Primitive().isString())) {
                    throw new IllegalArgumentException("Invalid bare value '" + bare.text() + "'. Must be Boolean, Number, Array, null or quoted String");
                }

                yield element;
            }
        };
    }
}
