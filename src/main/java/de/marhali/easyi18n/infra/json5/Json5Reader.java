package de.marhali.easyi18n.infra.json5;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileReader;
import de.marhali.json5.*;
import org.jetbrains.annotations.NotNull;


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
        if (element.isJson5Object()) {
            readObject(element.getAsJson5Object(), producer);
        } else {
            readValue(element, producer);
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

    private void readValue(@NotNull Json5Element element, @NotNull TranslationProducer producer) {
        String dumpedElement = element.toString(Json5FileProcessor.JSON5_MINIFY_OPTIONS); // Json5Element#toString() already provides escaped characters
        finallyProduceWithValue(producer, I18nValue.fromEscaped(dumpedElement));
    }
}
