package de.marhali.easyi18n.io.parser.yaml;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.exception.SyntaxException;
import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class YamlParserStrategy extends ParserStrategy {

    private static DumperOptions dumperOptions() {
        DumperOptions options = new DumperOptions();

        options.setIndent(2);
        options.setAllowUnicode(true);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        return options;
    }

    private static final Yaml YAML = new Yaml(dumperOptions());
    private final boolean isSaveAsStrings;

    public YamlParserStrategy(@NotNull ProjectSettings settings) {
        super(settings);
        this.isSaveAsStrings = settings.isSaveAsStrings();
    }

    @Override
    public void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception {
        data.addLocale(file.getLocale());

        VirtualFile vf = file.getVirtualFile();
        TranslationNode targetNode = super.getOrCreateTargetNode(file, data);

        try(Reader reader = new InputStreamReader(vf.getInputStream(), vf.getCharset())) {
            Map<String, Object> input;

            try {
                input = YAML.load(reader);
            } catch(YAMLException ex) {
                throw new SyntaxException(ex.getMessage(), file);
            }

            YamlMapper.read(file.getLocale(), input, targetNode);
        }
    }

    @Override
    public @Nullable String write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception {
        TranslationNode targetNode = super.getTargetNode(data, file);

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write(file.getLocale(), output, targetNode, isSaveAsStrings);

        return YAML.dumpAsMap(output);
    }
}
