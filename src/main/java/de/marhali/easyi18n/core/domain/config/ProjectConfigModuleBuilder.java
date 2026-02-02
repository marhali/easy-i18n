package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;

/**
 * Builder pattern to construct a {@link ProjectConfigModule}.
 *
 * @author marhali
 */
public class ProjectConfigModuleBuilder {

    private ModuleId id;
    private String pathTemplate;
    private FileCodec fileCodec;
    private String fileTemplate;
    private String keyTemplate;
    private String rootDirectory;
    private @Deprecated String defaultNamespace;
    private String i18nTemplate;
    private KeyNamingConvention keyNamingConvention;

    protected ProjectConfigModuleBuilder() {}

    protected ProjectConfigModuleBuilder(@NotNull ProjectConfigModule module) {
        this.id = module.id();
        this.pathTemplate = module.pathTemplate();
        this.fileCodec = module.fileCodec();
        this.fileTemplate = module.fileTemplate();
        this.keyTemplate = module.keyTemplate();
        this.rootDirectory = module.rootDirectory();
        this.defaultNamespace = module.defaultNamespace();
        this.i18nTemplate = module.i18nTemplate();
        this.keyNamingConvention = module.keyNamingConvention();
    }

    public @NotNull ProjectConfigModuleBuilder id(@NotNull ModuleId id) {
        this.id = id;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder pathTemplate(@NotNull String pathTemplate) {
        this.pathTemplate = pathTemplate;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder fileCodec(@NotNull FileCodec fileCodec) {
        this.fileCodec = fileCodec;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder fileTemplate(@NotNull String fileTemplate) {
        this.fileTemplate = fileTemplate;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder keyTemplate(@NotNull String keyTemplate) {
        this.keyTemplate = keyTemplate;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder rootDirectory(@NotNull String rootDirectory) {
        this.rootDirectory = rootDirectory;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder defaultNamespace(@NotNull String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder i18nTemplate(@NotNull String i18nTemplate) {
        this.i18nTemplate = i18nTemplate;
        return this;
    }

    public @NotNull ProjectConfigModuleBuilder keyNamingConvention(@NotNull KeyNamingConvention keyNamingConvention) {
        this.keyNamingConvention = keyNamingConvention;
        return this;
    }

    // Last

    public @NotNull ProjectConfigModule build() {
        return new ProjectConfigModule(
            id,
            pathTemplate,
            fileCodec,
            fileTemplate,
            keyTemplate,
            rootDirectory,
            defaultNamespace,
            i18nTemplate,
            keyNamingConvention
        );
    }
}
