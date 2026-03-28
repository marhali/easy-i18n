package de.marhali.easyi18n.infra.yaml;

import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.MutableI18nModule;
import de.marhali.easyi18n.core.domain.model.TranslationConsumer;
import de.marhali.easyi18n.core.domain.template.Templates;
import de.marhali.easyi18n.core.ports.FileProcessorPort;
import de.marhali.easyi18n.core.ports.FileSystemPort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * YAML file processor.
 * Uses the SnakeYAML library for reading and writing YAML files.
 *
 * @see <a href="https://bitbucket.org/snakeyaml/snakeyaml">snakeyaml</a>
 * @author marhali
 */
public class YamlFileProcessor implements FileProcessorPort {

    private static @NotNull Yaml buildYaml(int indent) {
        DumperOptions options = new DumperOptions();

        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setIndicatorIndent(indent);
        options.setIndentWithIndicator(true);
        options.setLineBreak(DumperOptions.LineBreak.UNIX);
        options.setSplitLines(false);
        options.setAllowUnicode(true);

        SafeConstructor constructor = new SafeConstructor(new LoaderOptions());

        Representer representer = new Representer(options);

        Resolver resolver = new Resolver() {
            @Override
            public void addImplicitResolvers() {
                // Disable all implicit resolvers (e.g. yes|no|on|off, ...)
            }
        };

        return new Yaml(
            constructor,
            representer,
            options,
            resolver
        );
    }

    protected static final @NotNull Yaml YAML = buildYaml(2);
    protected static final @NotNull Yaml YAML_MINIFY = buildYaml(0);

    private final @NotNull FileSystemPort fileSystemPort;

    public YamlFileProcessor(@NotNull FileSystemPort fileSystemPort) {
        this.fileSystemPort = fileSystemPort;
    }

    @Override
    public void readInto(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull MutableI18nModule store) throws IOException {
        String content = fileSystemPort.read(path.canonical());

        Map<@NotNull Object, @Nullable Object> rootMap;

        try {
            rootMap = YAML.load(content);
        } catch (YAMLException e) {
            throw new RuntimeException(e);
        }

        if (rootMap == null) {
            // Skip empty files
            return;
        }

        new YamlReader(path, templates, store).read(rootMap);
    }

    @Override
    public void writeFrom(@NotNull ProjectConfigModule config, @NotNull Templates templates, @NotNull I18nPath path, @NotNull Set<@NotNull TranslationConsumer> translations) throws IOException {
        YamlWriter writer = new YamlWriter(path, templates);

        writer.write(translations);

        String content = YAML.dumpAsMap(writer.getRootElement());

        fileSystemPort.write(path.canonical(), content);
    }
}
