package de.marhali.easyi18n.infra;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.TemplateValue;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.domain.template.file.LevelledFileTemplate;
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

    protected FileWriter(@NotNull I18nPath path, @NotNull Templates templates) {
        this.path = path;
        this.templates = templates;
    }

    /**
     * Maps the provided list of translation consumers to actual file consumable targets.
     * @param consumers Translation consumers
     * @return List of {@link TranslationTarget}'s
     */
    protected @NotNull List<@NotNull TranslationTarget> mapConsumersToSortedTargets(@NotNull Set<@NotNull TranslationConsumer> consumers) {
        return consumers.stream()
            .map(this::resolveTarget)
            .toList();
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
            LevelledFileTemplate fileTemplateLevel = templates.file().getAtLeveL(targetConsumer.level());

            // Determine needed placeholder parameters for this file level
            Set<String> neededParameterNames = fileTemplateLevel.getNeededParameterNames();

            I18nParamsBuilder paramsBuilder =  I18nParams.builder();

            // Resolve needed params at index
            for (String parameterName : neededParameterNames) {
                paramsBuilder.add(parameterName, targetConsumer.indexedParams().getParameterValueAtIndex(parameterName));
            }

            // Build file level out of parameters
            Set<@NotNull TemplateValue> variants = fileTemplateLevel.buildVariants(paramsBuilder.build());

            // A consumer should always define just one translation: key & locale -> value
            if (variants.size() != 1) {
                throw new IllegalStateException("Expecting exactly one variant for file template");
            }

            result.add(variants.iterator().next().value());

            // Move to next (child) file level and increase indexes for used parameters
            targetConsumer = targetConsumer.withChildren(neededParameterNames);
        }

        return result;
    }
}
