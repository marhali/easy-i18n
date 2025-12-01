package de.marhali.easyi18n.next_io.file;

import de.marhali.easyi18n.next_domain.I18nModuleStore;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;
import de.marhali.easyi18n.next_io.TranslationProducer;
import org.jetbrains.annotations.NotNull;

/**
 * File {@link FileCodec codec}-dependent mapping of content to translations.
 * Commonly used within a file {@link FileProcessor processor} to ease working with recursive data structures.
 * @author marhali
 */
public abstract class FileMapper {

    protected final @NotNull I18nModuleStore store;
    protected final @NotNull ModuleTemplate template;
    protected final @NotNull I18nFile file;

    protected FileMapper(
        @NotNull I18nModuleStore store,
        @NotNull ModuleTemplate template,
        @NotNull I18nFile file
    ) {
        this.store = store;
        this.template = template;
        this.file = file;
    }

    /**
     * Creates a root-level translation producer prefilled with file-based params.
     * @return {@link TranslationProducer}
     */
    protected @NotNull TranslationProducer createRootProducer() {
        return TranslationProducer.of(file.getParams(), 0);
    }
}
