package de.marhali.easyi18n.infra.properties;

import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.model.TranslationTarget;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.infra.FileWriter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Set;

/**
 * Properties specific writer.
 *
 * @author marhali
 */
public final class PropertiesWriter extends FileWriter {

    private final @NotNull LinkedProperties properties;

    PropertiesWriter(@NotNull I18nPath path, @NotNull Templates templates) {
        super(path, templates);
        this.properties = new LinkedProperties();
    }

    void write(@NotNull Set<@NotNull TranslationConsumer> translations) {
        for (TranslationTarget target : mapConsumersToSortedTargets(translations)) {
            write(target);
        }
    }

    @NotNull String serialize() throws IOException {
        StringWriter writer = new StringWriter();
        properties.store(writer);
        return writer.toString();
    }

    private void write(@NotNull TranslationTarget target) {
        // Properties are only flat files
        // TODO: canonicalHierarchy is not appropriate for our flat thingy yet
        // TODO: we could try one hierarchy level that is properly rebuild?
        assert target.canonicalHierarchy().size() == 1;
        properties.put(target.canonicalHierarchy().getFirst(), toPropertiesValue(target.value()));
    }

    private @NotNull Object toPropertiesValue(@NotNull I18nValue value) {
        return switch (value) {
            case I18nValue.Array array -> Arrays.stream(array.elements())
                .map(I18nValue.Primitive::getText)
                .toArray();
            case I18nValue.Primitive primitive -> primitive.getText();
        };
    }
}
