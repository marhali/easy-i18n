package de.marhali.easyi18n.io.parser.json5;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;

import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

/**
 * Json5 file format parser strategy
 * @author marhali
 */
public class Json5ParserStrategy extends ParserStrategy {

    private static final Json5 JSON5 = Json5.builder(builder ->
            builder.allowInvalidSurrogate().trailingComma().indentFactor(4).build());

    public Json5ParserStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);

        try (Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            Json5Element input = JSON5.parse(reader);
            if(input != null && input.isJsonObject()) {
                Json5Mapper.read(file.getLocale(), input.getAsJsonObject(), targetNode);
            }
        }
    }

    @Override
    public void write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception {
        TranslationNode targetNode = super.getTargetNode(data, file);

        Json5Object output = new Json5Object();
        Json5Mapper.write(file.getLocale(), output, Objects.requireNonNull(targetNode));

        VirtualFile vf = file.getVirtualFile();
        vf.setBinaryContent(JSON5.serialize(output).getBytes(vf.getCharset()));
    }
}
