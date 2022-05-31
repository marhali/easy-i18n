package de.marhali.easyi18n.io.parser.yaml;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;

import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Yaml / YML file format parser strategy.
 * @author marhali
 */
public class YamlParserStrategy extends ParserStrategy {

    public YamlParserStrategy(@NotNull ProjectSettings settings) {
        super(settings);
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            Section input = Section.parseToMap(reader);
            YamlMapper.read(file.getLocale(), input, targetNode);
        }
    }

    @Override
    public @NotNull String write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception {
        TranslationNode targetNode = super.getTargetNode(data, file);

        Section output = new MapSection();
        YamlMapper.write(file.getLocale(), output, targetNode);
        return Section.toString(output);
    }
}
