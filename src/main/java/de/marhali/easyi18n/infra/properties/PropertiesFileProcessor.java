package de.marhali.easyi18n.infra.properties;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileSystemPort;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

/**
 * Properties file processor.
 * Ues {@link LinkedProperties} for parsing and writing.
 *
 * @author marhali
 */
public class PropertiesFileProcessor implements FileProcessorPort {

    private final @NotNull FileSystemPort fileSystemPort;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public PropertiesFileProcessor(@NotNull FileSystemPort fileSystemPort, @NotNull ProjectConfigPort projectConfigPort) {
        this.fileSystemPort = fileSystemPort;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public void readInto(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull MutableI18nModule store) throws IOException {
        String content = fileSystemPort.read(path.canonical());

        LinkedProperties properties = new LinkedProperties();

        try {
            properties.load(new StringReader(content));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        new PropertiesReader(path, templates, store).read(properties);
    }

    @Override
    public void writeFrom(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull Set<@NotNull TranslationConsumer> translations) throws IOException {
        PropertiesWriter writer = new PropertiesWriter(path, templates, projectConfigPort);

        writer.write(translations);

        String content = writer.serialize();

        fileSystemPort.write(path.canonical(), content);
    }
}
