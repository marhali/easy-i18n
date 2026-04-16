package de.marhali.easyi18n.infra.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
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
import java.util.Set;

/**
 * JSON file processor.
 *
 * @see <a href="https://github.com/google/gson">gson</a>
 * @author marhali
 */
public class JsonFileProcessor implements FileProcessorPort {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .serializeNulls()
        .create();

    private final @NotNull FileSystemPort fileSystemPort;
    private final @NotNull ProjectConfigPort projectConfigPort;

    public JsonFileProcessor(@NotNull FileSystemPort fileSystemPort, @NotNull ProjectConfigPort projectConfigPort) {
        this.fileSystemPort = fileSystemPort;
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public void readInto(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull MutableI18nModule store) throws IOException {
        String content = fileSystemPort.read(path.canonical());

        JsonElement rootElement;

        try {
            rootElement = GSON.fromJson(content, JsonElement.class);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }

        if (rootElement == null) {
            // Skip empty files
            return;
        }

        new JsonReader(path, templates, store).read(rootElement);
    }

    @Override
    public void writeFrom(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull Set<@NotNull TranslationConsumer> translations) throws IOException {
        JsonWriter writer = new JsonWriter(path, templates, projectConfigPort);

        writer.write(translations);

        String content = GSON.toJson(writer.getRootElement());

        fileSystemPort.write(path.canonical(), content);
    }
}
