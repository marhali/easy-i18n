package de.marhali.easyi18n.config.project;

import de.marhali.easyi18n.config.project.preset.ProjectConfigModulePresetDefault;

import java.util.Objects;

/**
 * Describes a configured module that has i18n support.
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
     * File folder structure.
     */
    private String fileFolderPattern;

    /**
     * File content structure.
     */
    private String fileContentPattern;

    /**
     * Key structure.
     */
    private String keyPattern;

    /**
     * Root directory from which this configuration applies.
     */
    private String rootDirectory;

    // Editor configuration

    /**
     * Delimiter between namespace and the rest of the translation.
     */
    private String moduleDelimiter;

    /**
     * Delimiter between namespace and section keys.
     */
    private String namespaceDelimiter;

    /**
     * Delimiter between section keys.
     */
    private String sectionDelimiter;

    /**
     * Namespace to use as default if none is supplied. Can be an empty string to ignore this feature.
     */
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
        this.fileFolderPattern = origin.fileFolderPattern;
        this.fileContentPattern = origin.fileContentPattern;
        this.keyPattern = origin.keyPattern;
        this.rootDirectory = origin.rootDirectory;
        this.moduleDelimiter = origin.moduleDelimiter;
        this.namespaceDelimiter = origin.namespaceDelimiter;
        this.sectionDelimiter = origin.sectionDelimiter;
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

    public String getFileFolderPattern() {
        return fileFolderPattern;
    }

    public void setFileFolderPattern(String fileFolderPattern) {
        this.fileFolderPattern = fileFolderPattern;
    }

    public String getFileContentPattern() {
        return fileContentPattern;
    }

    public void setFileContentPattern(String fileContentPattern) {
        this.fileContentPattern = fileContentPattern;
    }

    public String getKeyPattern() {
        return keyPattern;
    }

    public void setKeyPattern(String keyPattern) {
        this.keyPattern = keyPattern;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getModuleDelimiter() {
        return moduleDelimiter;
    }

    public void setModuleDelimiter(String moduleDelimiter) {
        this.moduleDelimiter = moduleDelimiter;
    }

    public String getNamespaceDelimiter() {
        return namespaceDelimiter;
    }

    public void setNamespaceDelimiter(String namespaceDelimiter) {
        this.namespaceDelimiter = namespaceDelimiter;
    }

    public String getSectionDelimiter() {
        return sectionDelimiter;
    }

    public void setSectionDelimiter(String sectionDelimiter) {
        this.sectionDelimiter = sectionDelimiter;
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
        return "ProjectConfigResource{" +
            "name='" + name + '\'' +
            ", fileFolderPattern='" + fileFolderPattern + '\'' +
            ", fileContentPattern='" + fileContentPattern + '\'' +
            ", keyPattern='" + keyPattern + '\'' +
            ", rootDirectory='" + rootDirectory + '\'' +
            ", moduleDelimiter='" + moduleDelimiter + '\'' +
            ", namespaceDelimiter='" + namespaceDelimiter + '\'' +
            ", sectionDelimiter='" + sectionDelimiter + '\'' +
            ", defaultNamespace='" + defaultNamespace + '\'' +
            ", i18nTemplate='" + i18nTemplate + '\'' +
            ", keyNamingConvention=" + keyNamingConvention +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfigModule that = (ProjectConfigModule) o;
        return Objects.equals(name, that.name) && Objects.equals(fileFolderPattern, that.fileFolderPattern) && Objects.equals(fileContentPattern, that.fileContentPattern) && Objects.equals(keyPattern, that.keyPattern) && Objects.equals(rootDirectory, that.rootDirectory) && Objects.equals(moduleDelimiter, that.moduleDelimiter) && Objects.equals(namespaceDelimiter, that.namespaceDelimiter) && Objects.equals(sectionDelimiter, that.sectionDelimiter) && Objects.equals(defaultNamespace, that.defaultNamespace) && Objects.equals(i18nTemplate, that.i18nTemplate) && keyNamingConvention == that.keyNamingConvention;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, fileFolderPattern, fileContentPattern, keyPattern, rootDirectory, moduleDelimiter, namespaceDelimiter, sectionDelimiter, defaultNamespace, i18nTemplate, keyNamingConvention);
    }
}
