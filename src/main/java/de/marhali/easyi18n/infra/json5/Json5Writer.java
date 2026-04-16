package de.marhali.easyi18n.infra.json5;

import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.model.TranslationTarget;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import de.marhali.easyi18n.infra.FileWriter;
import de.marhali.json5.*;
import de.marhali.json5.stream.Json5Lexer;
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

    Json5Writer(@NotNull I18nPath path, @NotNull Templates templates, @NotNull ProjectConfigPort projectConfigPort) {
        super(path, templates, projectConfigPort);
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
        // Quickfix to properly parse Json5Primitive from a string value
        try (StringReader reader = new StringReader(value.raw().endsWith(",") ? value.raw() : value.raw() + ",")) {
            var lexer = new Json5Lexer(reader, Json5FileProcessor.JSON5_OPTIONS);
            return lexer.nextValue();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
