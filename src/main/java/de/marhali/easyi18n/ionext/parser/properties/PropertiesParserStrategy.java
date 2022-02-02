package de.marhali.easyi18n.ionext.parser.properties;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.properties.PropertiesMapper;
import de.marhali.easyi18n.io.properties.SortableProperties;
import de.marhali.easyi18n.ionext.parser.ParserStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Properties file format parser strategy.
 * @author marhali
 */
public class PropertiesParserStrategy extends ParserStrategy {

    public PropertiesParserStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws IOException {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);
        TranslationData targetData = new TranslationData(data.getLocales(), targetNode);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            SortableProperties input = new SortableProperties(this.settings.isSortKeys());
            input.load(reader);
            PropertiesMapper.read(file.getLocale(), input, targetData);
        }
    }

    @Override
    public void write(@NotNull TranslationData data, @NotNull TranslationFile file) throws IOException {
        TranslationNode targetNode = super.getTargetNode(data, file);
        TranslationData targetData = new TranslationData(data.getLocales(), targetNode);

        SortableProperties output = new SortableProperties(this.settings.isSortKeys());
        PropertiesMapper.write(file.getLocale(), output, targetData);

        try(StringWriter writer = new StringWriter()) {
            output.store(writer, null);

            VirtualFile vf = file.getVirtualFile();
            vf.setBinaryContent(writer.toString().getBytes(vf.getCharset()));
        }
    }
}
