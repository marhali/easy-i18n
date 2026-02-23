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

        Map<@NotNull I18nPath, @NotNull Set<TranslationConsumer>> translationsByPath = new HashMap<>();

        // Iterate over all translation keys within this module
        for (Map.Entry<@NotNull I18nKey, @NotNull I18nContent> translationEntry : store.translations().entrySet()) {
            I18nParams keyParams = templates.key().toParams(translationEntry.getKey());
            Set<LocaleId> localeIds = store.translations().get(translationEntry.getKey()).values().keySet();

            // Collect necessary params for this translation key (key params & used localeIds)
            I18nParams params = I18nParams.builder()
                .addAll(keyParams)
                .add(I18nBuiltinParam.LOCALE, localeIds.stream().map(LocaleId::tag).toArray(String[]::new))
                .build();

            // Build all paths to store this translation
            var paths = templates.path().buildVariants(params);

            var content = translationEntry.getValue();

            // Iterate over all translation file paths
            for (I18nPath path : paths) {
                List<String> pathAdvisedLocales = path.params().get(I18nBuiltinParam.LOCALE.getParameterName());
                var consumersInPath = translationsByPath.computeIfAbsent(path, (_path) -> new HashSet<>());

                // Stock path with locale relevant translation values
                for (Map.Entry<LocaleId, I18nValue> contentEntry : content.values().entrySet()) {
                    // If the path does not specify any locale we will add the consumer and expect locale handling within the file
                    if (pathAdvisedLocales == null || pathAdvisedLocales.contains(contentEntry.getKey().tag())) {

                        // Build keyParamsIndex with zeroed values for all relevant (non path related) params
                        var keyParamsIndex = new HashMap<String, Integer>();
                        for (Map.Entry<String, List<String>> keyParamsEntry : keyParams.params().entrySet()) {
                            if (!path.params().has(keyParamsEntry.getKey())) {
                                keyParamsIndex.put(keyParamsEntry.getKey(), 0);
                            }
                        }

                        var consumer = new TranslationConsumer(0, keyParams, keyParamsIndex, contentEntry.getKey(), contentEntry.getValue(), content.comment());
                        consumersInPath.add(consumer);
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
                // TODO: improve ex handling
                throw new RuntimeException(e);
            }
        }

        // TODO: might be useful: track I18nPath's from last read and delete if write does not care about them anymore
    }
}
