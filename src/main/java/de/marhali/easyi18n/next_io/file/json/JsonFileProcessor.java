package de.marhali.easyi18n.next_io.file.json;

import com.google.gson.*;
import de.marhali.easyi18n.config.project.ProjectConfig;
import de.marhali.easyi18n.config.project.ProjectConfigModule;
import de.marhali.easyi18n.next_domain.I18nProjectStore;
import de.marhali.easyi18n.next_io.I18nFile;
import de.marhali.easyi18n.next_io.ModuleTemplate;
import de.marhali.easyi18n.next_io.TranslationConsumer;
import de.marhali.easyi18n.next_io.file.FileProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;

/**
 * @author marhali
 */
public class JsonFileProcessor extends FileProcessor {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    protected JsonFileProcessor(
        @NotNull ProjectConfig projectConfig,
        @NotNull ProjectConfigModule moduleConfig,
        @NotNull ModuleTemplate moduleTemplate,
        @NotNull I18nProjectStore store
    ) {
        super(projectConfig, moduleConfig, moduleTemplate, store);
    }

    @Override
    public void read(@NotNull I18nFile file) throws Exception {
        super.detectPathLocale(file);

        var module = store.getOrCreateModule(moduleConfig.getName());
        var vf = file.getFile();

        try (Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            JsonElement input;

            try {
                input = GSON.fromJson(reader, JsonElement.class);
            } catch (JsonSyntaxException ex) {
                // TODO: proper typed ex handling
                throw ex;
            }

            if (input == null) {
                return;
            }

            new JsonReader(module, moduleTemplate, file).read(input);
        }
    }

    @Override
    public void write(@NotNull I18nFile file, @NotNull Set<TranslationConsumer> consumers) throws Exception {
        // TODO: maybe use store in tree format (I18nNode) to build the output object
        // TODO: maybe use JsonReader and JsonWriter as helper classes for each file processor

        var module = store.getOrCreateModule(moduleConfig.getName());
        var vf = file.getFile();

        var mapper = new JsonWriter(module, moduleTemplate, file);
        System.out.println(vf);
        System.out.println(file.getParams());
        for (TranslationConsumer consumer : consumers) {
            //mapper.write(consumer);
            System.out.println(consumer);
        }

        System.out.println(GSON.toJson(mapper.getRootElement()));

        /*
        try (Writer writer = new OutputStreamWriter(vf.getOutputStream(this), vf.getCharset())) {
            GSON.toJson(output, writer);
        }

         */
    }
}
