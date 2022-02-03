package de.marhali.easyi18n.ionext.parser.yaml;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.ionext.parser.ParserStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;

import org.jetbrains.annotations.NotNull;
import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Yaml / YML file format parser strategy.
 * @author marhali
 */
public class YamlParserStrategy extends ParserStrategy {

    public YamlParserStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws IOException {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            Section input = Section.parseToMap(reader);
            YamlMapper.read(file.getLocale(), input, targetNode);
        }
    }

    @Override
    public void write(@NotNull TranslationData data, @NotNull TranslationFile file) throws IOException {
        TranslationNode targetNode = super.getTargetNode(data, file);

        Section output = new MapSection();
        YamlMapper.write(file.getLocale(), output, targetNode);

        VirtualFile vf = file.getVirtualFile();
        vf.setBinaryContent(Section.toString(output).getBytes(vf.getCharset()));
    }
}
