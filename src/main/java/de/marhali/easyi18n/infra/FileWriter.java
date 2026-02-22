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
     * Computes the hierarchical file levels for the given translation.
     * @param consumer Translation
     * @return List of hierarchical file levels
     */
    protected @NotNull List<@NotNull String> toFilePath(@NotNull TranslationConsumer consumer) {
        List<String> result = new ArrayList<>();

        TranslationConsumer targetConsumer = consumer;

        while (!targetConsumer.isIndexed()) {
            LevelledFileTemplate fileTemplateLevel = templates.file().getAtLeveL(targetConsumer.level());

            // Determine needed placeholder parameters for this file level
            Set<String> neededParameterNames = fileTemplateLevel.getNeededParameterNames();

            I18nParamsBuilder paramsBuilder =  I18nParams.builder();

            // If locale might be necessary, just add it
            paramsBuilder.add(I18nBuiltinParam.LOCALE, targetConsumer.localeId().tag());

            // Add path params which might be necessary
            paramsBuilder.addAll(path.params());

            // Resolve needed parameters with index in mind
            for (String parameterName : neededParameterNames) {
                paramsBuilder.add(parameterName, resolveIndexedKeyParameter(targetConsumer, parameterName));
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

    /**
     * Resolves parameter value at provided index.
     *
     * @param consumer Translation
     * @param parameterName Parameter name
     * @return Parameter value
     */
    private @NotNull String resolveIndexedKeyParameter(@NotNull TranslationConsumer consumer, @NotNull String parameterName) {
        var index = consumer.keyParamsIndex().getOrDefault(parameterName, 0);
        var parameterValues = consumer.keyParams().get(parameterName);

        if (parameterValues == null || parameterValues.size() <= index) {
            throw new IllegalStateException("Missing parameter value at index " + index + " for parameter " + parameterName);
        }

        return parameterValues.get(index);
    }
}
