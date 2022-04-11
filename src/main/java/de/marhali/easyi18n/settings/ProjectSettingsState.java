package de.marhali.easyi18n.settings;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.settings.presets.DefaultPreset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the project-specific configuration of this plugin.
 * @author marhali
 */
public class ProjectSettingsState implements ProjectSettings {

    private static final ProjectSettings defaults = new DefaultPreset();

    // Resource Configuration
    private String localesDirectory;
    private FolderStrategyType folderStrategy;
    private ParserStrategyType parserStrategy;
    private String filePattern;

    private Boolean sorting;

    // Editor configuration
    private String namespaceDelimiter;
    private String sectionDelimiter;
    private String contextDelimiter;
    private String pluralDelimiter;
    private String defaultNamespace;
    private String previewLocale;

    private Boolean nestedKeys;
    private Boolean assistance;

    public ProjectSettingsState() {}

    @Override
    public @Nullable String getLocalesDirectory() {
        return localesDirectory != null ? localesDirectory : defaults.getLocalesDirectory();
    }

    @Override
    public @NotNull FolderStrategyType getFolderStrategy() {
        return folderStrategy != null ? folderStrategy : defaults.getFolderStrategy();
    }

    @Override
    public @NotNull ParserStrategyType getParserStrategy() {
        return parserStrategy != null ? parserStrategy : defaults.getParserStrategy();
    }

    @Override
    public @NotNull String getFilePattern() {
        return filePattern != null ? filePattern : defaults.getFilePattern();
    }

    @Override
    public boolean isSorting() {
        return sorting != null ? sorting : defaults.isSorting();
    }

    @Override
    public @Nullable String getNamespaceDelimiter() {
        return namespaceDelimiter != null ? namespaceDelimiter : defaults.getNamespaceDelimiter();
    }

    @Override
    public @NotNull String getSectionDelimiter() {
        return sectionDelimiter != null ? sectionDelimiter : defaults.getSectionDelimiter();
    }

    @Override
    public @Nullable String getContextDelimiter() {
        return contextDelimiter != null ? contextDelimiter : defaults.getContextDelimiter();
    }

    @Override
    public @Nullable String getPluralDelimiter() {
        return pluralDelimiter != null ? pluralDelimiter : defaults.getPluralDelimiter();
    }

    @Nullable
    @Override
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    @Override
    public @NotNull String getPreviewLocale() {
        return previewLocale != null ? previewLocale : defaults.getPreviewLocale();
    }

    @Override
    public boolean isNestedKeys() {
        return nestedKeys != null ? nestedKeys : defaults.isNestedKeys();
    }

    @Override
    public boolean isAssistance() {
        return assistance != null ? assistance : defaults.isAssistance();
    }

    public void setLocalesDirectory(String localesDirectory) {
        this.localesDirectory = localesDirectory;
    }

    public void setFolderStrategy(FolderStrategyType folderStrategy) {
        this.folderStrategy = folderStrategy;
    }

    public void setParserStrategy(ParserStrategyType parserStrategy) {
        this.parserStrategy = parserStrategy;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public void setSorting(Boolean sorting) {
        this.sorting = sorting;
    }

    public void setNamespaceDelimiter(String namespaceDelimiter) {
        this.namespaceDelimiter = namespaceDelimiter;
    }

    public void setSectionDelimiter(String sectionDelimiter) {
        this.sectionDelimiter = sectionDelimiter;
    }

    public void setContextDelimiter(String contextDelimiter) {
        this.contextDelimiter = contextDelimiter;
    }

    public void setPluralDelimiter(String pluralDelimiter) {
        this.pluralDelimiter = pluralDelimiter;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public void setPreviewLocale(String previewLocale) {
        this.previewLocale = previewLocale;
    }

    public void setNestedKeys(Boolean nestedKeys) {
        this.nestedKeys = nestedKeys;
    }

    public void setAssistance(Boolean assistance) {
        this.assistance = assistance;
    }
}
