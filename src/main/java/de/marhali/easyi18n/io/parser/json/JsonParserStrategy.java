package de.marhali.easyi18n.io.parser.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.exception.SyntaxException;
import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

/**
 * Json file format parser strategy.
 * @author marhali
 */
public class JsonParserStrategy extends ParserStrategy {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final boolean isSaveAsStrings;
    public JsonParserStrategy(@NotNull ProjectSettings settings) {
        super(settings);
        this.isSaveAsStrings = settings.isSaveAsStrings();
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            JsonObject input;

            try {
                input = GSON.fromJson(reader, JsonObject.class);
            } catch (JsonSyntaxException ex) {
                throw new SyntaxException(ex.getMessage(), file);
            }

            if(input != null) { // @input is null if file is completely empty
                JsonMapper.read(file.getLocale(), input, targetNode);
            }
        }
    }

    @Override
    public @NotNull String write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception {
        TranslationNode targetNode = super.getTargetNode(data, file);

        JsonObject output = new JsonObject();
        JsonMapper.write(file.getLocale(), output, Objects.requireNonNull(targetNode), isSaveAsStrings);

        return GSON.toJson(output);
    }
}
