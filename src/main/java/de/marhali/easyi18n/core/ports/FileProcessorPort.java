package de.marhali.easyi18n.core.ports;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.template.Templates;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * File content processor port.
 *
 * @author marhali
 */
public interface FileProcessorPort {
    /**
     * Reads the specified translation file and apply its content to the given store.
     * @param config Module config
     * @param templates Templates
     * @param path Translation file path
     * @param store Store
     * @throws IOException Error whilst processing the file
     */
    void readInto(
        @NotNull ProjectConfigModule config,
        @NotNull Templates templates,
        @NotNull I18nPath path,
        @NotNull MutableI18nModule store
    ) throws IOException;

    /**
     * Writes to the specified translation file and applies all provided translations.
     * @param config Module config
     * @param templates Templates
     * @param path Translation file path
     * @param translations Translations to store inside
     * @throws IOException Error whilst processing the file
     */
    void writeFrom(
        @NotNull ProjectConfigModule config,
        @NotNull Templates templates,
        @NotNull I18nPath path,
        @NotNull Set<@NotNull TranslationConsumer> translations
    ) throws IOException;
}
