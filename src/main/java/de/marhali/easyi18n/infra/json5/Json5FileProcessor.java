package de.marhali.easyi18n.infra.json5;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileSystemPort;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.config.Json5Options;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Set;

/**
 * JSON5 file processor.
 * Uses the json5-java library for reading and writing JSON5 files.
 *
 * @see <a href="https://github.com/marhali/json5-java">json5-java</a>
 * @author marhali
 */
public class Json5FileProcessor implements FileProcessorPort {

    protected static final @NotNull Json5Options JSON5_OPTIONS = Json5Options.builder()
        .allowInvalidSurrogates()
        .allowBinaryLiterals()
        .allowOctalLiterals()
        .allowNaN()
        .allowInfinity()
        .allowLongUnicodeEscapes()
        .allowHexFloatingLiterals()
        .trailingComma()
        .indentFactor(2)
        .quoteless()
        .build();

    protected static final @NotNull Json5Options JSON5_MINIFY_OPTIONS = Json5Options.builder()
        .allowInvalidSurrogates()
        .allowBinaryLiterals()
        .allowOctalLiterals()
        .allowNaN()
        .allowInfinity()
        .allowLongUnicodeEscapes()
        .allowHexFloatingLiterals()
        .trailingComma()
        .indentFactor(0)
        .quoteless()
        .build();

    private static final @NotNull Json5 JSON5 = new Json5(JSON5_OPTIONS);

    private final @NotNull FileSystemPort fileSystemPort;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public Json5FileProcessor(@NotNull FileSystemPort fileSystemPort, @NotNull ProjectConfigPort projectConfigPort) {
        this.fileSystemPort = fileSystemPort;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public void readInto(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull MutableI18nModule store) throws IOException {
        String content = fileSystemPort.read(path.canonical());

        Json5Element rootElement;

        try {
            rootElement = JSON5.parse(content);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        if (rootElement == null) {
            // Skip empty files
            return;
        }

        new Json5Reader(path, templates, store).read(rootElement);
    }

    @Override
    public void writeFrom(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull Set<@NotNull TranslationConsumer> translations) throws IOException {
        Json5Writer writer = new Json5Writer(path, templates, projectConfigPort);

        writer.write(translations);

        String content = JSON5.serialize(writer.getRootElement());

        fileSystemPort.write(path.canonical(), content);
    }
}
