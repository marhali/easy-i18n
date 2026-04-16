package de.marhali.easyi18n.infra;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.TemplateElement;
import de.marhali.easyi18n.core.domain.template.TemplateValue;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.domain.template.file.LevelledFileTemplate;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * File writer superclass.
 *
 * @author marhali
 */
public abstract class FileWriter {

    protected final @NotNull I18nPath path;
    protected final @NotNull Templates templates;
    private final @NotNull ProjectConfigPort projectConfigPort;

    protected FileWriter(@NotNull I18nPath path, @NotNull Templates templates, @NotNull ProjectConfigPort projectConfigPort) {
        this.path = path;
        this.templates = templates;
        this.projectConfigPort = projectConfigPort;
    }

    /**
     * Maps the provided list of translation consumers to actual file consumable targets.
     * @param consumers Translation consumers
     * @return List of {@link TranslationTarget}'s
     */
    protected @NotNull List<@NotNull TranslationTarget> mapConsumersToSortedTargets(@NotNull Set<@NotNull TranslationConsumer> consumers) {
        var stream = consumers.stream().map(this::resolveTarget);

        // If sorting is enabled also sort TranslationTarget to ensure that the files are also properly sorted
        // Uses the file path (List<String>) for ordering under the hood. See TranslationTarget#compareTo
        if (projectConfigPort.read().sorting()) {
            stream = stream.sorted();
        }

        return stream.toList();
    }

    /**
     * Resolves the 1-to-1 translation target for the given consumer.
     * @param consumer Translation consumer
     * @return {@link TranslationTarget}
     */
    protected @NotNull TranslationTarget resolveTarget(@NotNull TranslationConsumer consumer) {
        return new TranslationTarget(toFilePath(consumer), consumer.value(), consumer.comment());
    }

    /**
     * Computes the hierarchical file levels for the given translation.
     * @param consumer Translation
     * @return List of hierarchical file levels
     */
    protected @NotNull List<@NotNull String> toFilePath(@NotNull TranslationConsumer consumer) {
        List<String> result = new ArrayList<>();

        TranslationConsumer targetConsumer = consumer;

        while (!targetConsumer.isFullyIndexed()) {
            LevelledFileTemplate fileTemplateLevel = templates.file().getAtLevel(targetConsumer.level());

            // Determine needed placeholder parameters for this file level
            Set<TemplateElement.Placeholder> neededParameters = fileTemplateLevel.getNeededParameters();

            I18nParamsBuilder paramsBuilder =  I18nParams.builder();

            for (TemplateElement.Placeholder neededParameter : neededParameters) {
                String neededParameterName = neededParameter.name();

                if (neededParameter.hasDelimiter()) {
                    // Values for this parameter should be joined by a delimiter
                    paramsBuilder.add(neededParameterName, targetConsumer.indexedParams().getAllParameterValues(neededParameterName));
                } else {
                    // Resolve needed value at current index
                    paramsBuilder.add(neededParameterName,
                        targetConsumer.indexedParams().getParameterValueAtIndex(neededParameterName));
                }
            }

            // Build file level out of parameters
            Set<@NotNull TemplateValue> variants = fileTemplateLevel.buildVariants(paramsBuilder.build());

            // A consumer should always define just one translation: key & locale -> value
            if (variants.size() != 1) {
                throw new IllegalStateException("Expecting exactly one variant for file template");
            }

            result.add(variants.iterator().next().value());

            // Move to next (child) file level and increase indexes for used parameters
            targetConsumer = targetConsumer.withChildren(neededParameters);
        }

        return result;
    }
}
