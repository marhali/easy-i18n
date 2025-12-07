package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_domain.I18nProjectStore;
import de.marhali.easyi18n.next_io.I18nBuiltinParam;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;
import de.marhali.easyi18n.next_io.TranslationConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Each file {@link FileCodec codec} must implement a processor capable of
 * reading and writing translations for that file type.
 *
 * @author marhali
 */
public abstract class FileProcessor {

    public static FileProcessor from(
        @NotNull ProjectConfig projectConfig,
        @NotNull ProjectConfigModule moduleConfig,
        @NotNull ModuleTemplate moduleTemplate,
        @NotNull I18nProjectStore store
    ) throws Exception {
        var constructor = moduleConfig.getFileCodec().getFileProcessorClass().getDeclaredConstructor(
            ProjectConfig.class,
            ProjectConfigModule.class,
            ModuleTemplate.class,
            I18nProjectStore.class
        );

        constructor.setAccessible(true); // Constructor is protected

        return constructor.newInstance(projectConfig, moduleConfig, moduleTemplate, store);
    }

    protected final @NotNull ProjectConfig projectConfig;
    protected final @NotNull ProjectConfigModule moduleConfig;
    protected final @NotNull ModuleTemplate moduleTemplate;
    protected final @NotNull I18nProjectStore store;

    protected FileProcessor(
        @NotNull ProjectConfig projectConfig,
        @NotNull ProjectConfigModule moduleConfig,
        @NotNull ModuleTemplate moduleTemplate,
        @NotNull I18nProjectStore store
    ) {
        this.projectConfig = projectConfig;
        this.moduleConfig = moduleConfig;
        this.moduleTemplate = moduleTemplate;
        this.store = store;
    }

    /**
     * Parses the specified translation file and imports found translations into the {@link I18nProjectStore store}.
     *
     * @param file Translation file to parse
     * @throws Exception Exception that occurs during file parsing
     */
    public abstract void read(@NotNull I18nFile file) throws Exception;

    /**
     * Writes translations from the {@link I18nProjectStore store} to the specified file.
     *
     * @param file Translation file to write
     * @param consumers Translations which belong to this file
     * @throws Exception Exception that occurs during file writing
     */
    public abstract void write(@NotNull I18nFile file, @NotNull Set<TranslationConsumer> consumers) throws Exception;

    /**
     * Checks if the provided {@link I18nFile file} contains a locale parameter in its file path.
     * @param file The translation file to check
     */
    protected void detectPathLocale(@NotNull I18nFile file) {
        var localeParamName = I18nBuiltinParam.LOCALE.getParamName();
        var locales = file.getParams().get(localeParamName);

        if (locales == null) {
            return;
        }

        for (String locale : locales) {
            this.store.getOrCreateModule(this.moduleConfig.getName()).addLocale(locale);
        }
    }
}
