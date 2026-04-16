package de.marhali.easyi18n.infra.yaml;

import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.I18nValue;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.model.TranslationTarget;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import de.marhali.easyi18n.infra.FileWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * YAML specific writer.
 *
 * @author marhali
 */
public final class YamlWriter extends FileWriter {

    private final @NotNull Map<@NotNull String, @Nullable Object> rootElement;

    YamlWriter(@NotNull I18nPath path, @NotNull Templates templates, @NotNull ProjectConfigPort projectConfigPort) {
        super(path, templates, projectConfigPort);
        this.rootElement = new LinkedHashMap<>();
    }

    void write(@NotNull Set<@NotNull TranslationConsumer> translations) {
        for (TranslationTarget target : mapConsumersToSortedTargets(translations)) {
            write(target);
        }
    }

    @SuppressWarnings("unchecked")
    private void write(@NotNull TranslationTarget target) {
        Iterator<String> hierarchyIterator = target.canonicalHierarchy().iterator();

        Map<String, Object> targetObject = rootElement;
        String memberName = null;

        while (hierarchyIterator.hasNext()) {
            memberName = hierarchyIterator.next();
            boolean hasChild = hierarchyIterator.hasNext();

            if (hasChild) {
                if (targetObject.containsKey(memberName)) {
                    targetObject = (Map<String, Object>) targetObject.get(memberName);
                } else {
                    var newTargetObject = new LinkedHashMap<String, Object>();
                    targetObject.put(memberName, newTargetObject);
                    targetObject = newTargetObject;
                }
            }
        }

        if (memberName == null) {
            throw new IllegalStateException("Missing last hierarchical property for: " + target);
        }

        targetObject.put(memberName, toYamlElement(target.value()));
    }

    @NotNull Map<@NotNull String, @Nullable Object> getRootElement() {
        return rootElement;
    }

    private @Nullable Object toYamlElement(@NotNull I18nValue value) {
        String unescapedValue = value.toUnescaped();
        unescapedValue = unescapedValue + "\n"; // Add \n again as it has been cut out in YamlReader
        return YamlFileProcessor.YAML_MINIFY.load(unescapedValue);
    }
}
