package de.marhali.easyi18n.config.project;

import de.marhali.easyi18n.config.project.preset.ProjectConfigModulePresetDefault;

import java.util.Objects;

/**
 * Describes a configured module that has i18n support.
 *
 * @author marhali
 */
public class ProjectConfigModule {

    public static ProjectConfigModule fromDefaultPreset() {
        return new ProjectConfigModulePresetDefault().applyPreset(null);
    }

    /**
     * A descriptive name that identifies this resource configuration.
     */
    private String name;

    // Resource configuration

    /**
     * File path template syntax.
     */
    private String pathTemplate;

    /**
     * File content template syntax.
     */
    private String fileTemplate;

    /**
     * Key template syntax.
     */
    private String keyTemplate;

    /**
     * Root directory from which this module configuration applies.
     */
    private String rootDirectory;

    // Editor configuration

    /**
     * Namespace to use as default if none is supplied. Can be an empty string to ignore this feature.
     * @deprecated We do not want to force the user to label this functionality as namespace, the user should define a list of key(prefixes) that should be used
     */
    @Deprecated
    private String defaultNamespace;

    /**
     * Template to apply for translation message extraction.
     */
    private String i18nTemplate;

    /**
     * Defines the used key naming convention.
     */
    private KeyNamingConvention keyNamingConvention;

    /**
     * Creates a deep copy from the provided {@link ProjectConfigModule config}.
     * @param origin Config to copy from
     */
    public ProjectConfigModule(ProjectConfigModule origin) {
        this.name = origin.name;
        this.pathTemplate = origin.pathTemplate;
        this.fileTemplate = origin.fileTemplate;
        this.keyTemplate= origin.keyTemplate;
        this.rootDirectory = origin.rootDirectory;
        this.defaultNamespace = origin.defaultNamespace;
        this.i18nTemplate = origin.i18nTemplate;
        this.keyNamingConvention = origin.keyNamingConvention;
    }

    public ProjectConfigModule() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathTemplate() {
        return pathTemplate;
    }

    public void setPathTemplate(String pathTemplate) {
        this.pathTemplate = pathTemplate;
    }

    public String getFileTemplate() {
        return fileTemplate;
    }

    public void setFileTemplate(String fileTemplate) {
        this.fileTemplate = fileTemplate;
    }

    public String getKeyTemplate() {
        return keyTemplate;
    }

    public void setKeyTemplate(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    /**
     * Checks whether a default namespace has been specified and this feature should be used or not.
     * @return {@code true} if a default namespace has been configured otherwise {@code false}.
     */
    public boolean hasDefaultNamespace() {
        return defaultNamespace.isEmpty();
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getI18nTemplate() {
        return i18nTemplate;
    }

    public void setI18nTemplate(String i18nTemplate) {
        this.i18nTemplate = i18nTemplate;
    }

    public KeyNamingConvention getKeyNamingConvention() {
        return keyNamingConvention;
    }

    public void setKeyNamingConvention(KeyNamingConvention keyNamingConvention) {
        this.keyNamingConvention = keyNamingConvention;
    }

    @Override
    public String toString() {
        return "ProjectConfigModule{" +
            "name='" + name + '\'' +
            ", pathTemplate='" + pathTemplate + '\'' +
            ", fileTemplate='" + fileTemplate + '\'' +
            ", keyTemplate='" + keyTemplate + '\'' +
            ", rootDirectory='" + rootDirectory + '\'' +
            ", defaultNamespace='" + defaultNamespace + '\'' +
            ", i18nTemplate='" + i18nTemplate + '\'' +
            ", keyNamingConvention=" + keyNamingConvention +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigModule module = (ProjectConfigModule) o;
        return Objects.equals(name, module.name) && Objects.equals(pathTemplate, module.pathTemplate) && Objects.equals(fileTemplate, module.fileTemplate) && Objects.equals(keyTemplate, module.keyTemplate) && Objects.equals(rootDirectory, module.rootDirectory) && Objects.equals(defaultNamespace, module.defaultNamespace) && Objects.equals(i18nTemplate, module.i18nTemplate) && keyNamingConvention == module.keyNamingConvention;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pathTemplate, fileTemplate, keyTemplate, rootDirectory, defaultNamespace, i18nTemplate, keyNamingConvention);
    }
}
