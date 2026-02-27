package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileProcessorRegistryPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * Module persistor using the underlying io ports.
 *
 * @author marhali
 */
public class DefaultModulePersistor implements ModulePersistor {

    private final @NotNull CachedModuleTemplates cachedModuleTemplates;
    private final @NotNull FileProcessorRegistryPort fileProcessorRegistryPort;

    public DefaultModulePersistor(@NotNull CachedModuleTemplates cachedModuleTemplates, @NotNull FileProcessorRegistryPort fileProcessorRegistryPort) {
        this.cachedModuleTemplates = cachedModuleTemplates;
        this.fileProcessorRegistryPort = fileProcessorRegistryPort;
    }

    @Override
    public void persistFrom(@NotNull ProjectConfigModule configModule, @NotNull I18nModule store) {
        Templates templates = cachedModuleTemplates.resolve(configModule.id());

        // Collect consumable translations for every translation file - use a linked map to keep store order
        Map<@NotNull I18nPath, @NotNull Set<TranslationConsumer>> translationsByPath = new LinkedHashMap<>();

        // Iterate over all translation keys within this module
        for (Map.Entry<@NotNull I18nKey, @NotNull I18nContent> translationEntry : store.translations().entrySet()) {
            // Retrieve all params specified by the translation key
            I18nParams keyParams = templates.key().toParams(translationEntry.getKey());

            I18nContent content = translationEntry.getValue();
            Set<LocaleId> localeIds = content.values().keySet();

            // Collect all params for this translation entry (keyParams + target locale's)
            I18nParams allParams = I18nParams.builder()
                .addAll(keyParams)
                .add(I18nBuiltinParam.LOCALE, localeIds.stream().map(LocaleId::tag).toArray(String[]::new))
                .build();

            // Build all paths to store this translation
            var paths = templates.path().buildVariants(allParams);

            // Iterate over all paths for this translation
            for (I18nPath path : paths) {
                // Compute storable set for consumers for this path - use linked set to keep store order
                var consumersForPath = translationsByPath.computeIfAbsent(path, (_path) -> new LinkedHashSet<>());

                // Nullable list of locales that the path specifies
                var pathSpecifiedLocales = path.params().get(I18nBuiltinParam.LOCALE);

                // Reduce allParams by path params to retrieve file content specific params
                var fileParams = allParams.toBuilder()
                    .removeKeys(path.params().keySet())
                    .build();

                // Populate path with translation values (consumers)
                for (Map.Entry<LocaleId, I18nValue> localeEntry : content.values().entrySet()) {
                    // Only add localeEntry if the path does not specify any locales or contains our localeEntry localeId
                    if (pathSpecifiedLocales == null || pathSpecifiedLocales.contains(localeEntry.getKey().tag())) {
                        consumersForPath.add(TranslationConsumer.fromNew(
                            // Edge case: If the path does not specify any locale, the file must specify one
                            pathSpecifiedLocales == null
                                ? fileParams.toBuilder()
                                    .put(I18nBuiltinParam.LOCALE, List.of(localeEntry.getKey().tag()))
                                    .build()
                                : fileParams,
                            localeEntry.getValue(),
                            content.comment()
                        ));
                    }
                }
            }
        }

        FileProcessorPort fileProcessorPort = fileProcessorRegistryPort.get(configModule.fileCodec());

        // Write to paths with mapped translations
        for (I18nPath path : translationsByPath.keySet()) {
            try {
                fileProcessorPort.writeFrom(configModule, templates, path, translationsByPath.get(path));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // TODO: might be useful: track I18nPath's from last read and delete if write does not care about them anymore
    }
}
