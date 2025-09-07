package de.marhali.easyi18n.io.parser.properties;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.exception.SyntaxException;
import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.util.KeyPathConverter;

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

    private final @NotNull KeyPathConverter converter;
    private final boolean isSaveAsStrings;

    public PropertiesParserStrategy(@NotNull ProjectSettings settings) {
        super(settings);
        this.converter = new KeyPathConverter(settings);
        this.isSaveAsStrings = settings.isSaveAsStrings();
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);
        TranslationData targetData = new TranslationData(data.getLocales(), targetNode);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            SortableProperties input = new SortableProperties(this.settings.isSorting());

            try {
                input.load(reader);
            } catch(IOException ex) {
                throw new SyntaxException(ex.getMessage(), file);
            }

            PropertiesMapper.read(file.getLocale(), input, targetData, converter);
        }
    }

    @Override
    public @NotNull String write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception {
        TranslationNode targetNode = super.getTargetNode(data, file);
        TranslationData targetData = new TranslationData(data.getLocales(), targetNode);

        SortableProperties output = new SortableProperties(this.settings.isSorting());
        PropertiesMapper.write(file.getLocale(), output, targetData, converter, isSaveAsStrings);

        try(StringWriter writer = new StringWriter()) {
            output.store(writer);
            return writer.toString();
        }
    }
}
