package de.marhali.easyi18n.settings;

import com.intellij.util.xmlb.annotations.Property;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.settings.presets.DefaultPreset;

import de.marhali.easyi18n.settings.presets.NamingConvention;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the project-specific configuration of this plugin.
 *
 * @author marhali
 */
public class ProjectSettingsState implements ProjectSettings {

    // Resource Configuration
    @Property
    private String localesDirectory;
    @Property
    private FolderStrategyType folderStrategy;
    @Property
    private ParserStrategyType parserStrategy;
    @Property
    private String filePattern;

    @Property
    private Boolean includeSubDirs;
    @Property
    private boolean sorting;

    // Editor configuration
    @Property
    private String namespaceDelimiter;
    @Property
    private String sectionDelimiter;
    @Property
    private String contextDelimiter;
    @Property
    private String pluralDelimiter;
    @Property
    private String defaultNamespace;
    @Property
    private String previewLocale;

    @Property
    private Boolean nestedKeys;
    @Property
    private Boolean assistance;

    // Experimental configuration
    @Property
    private Boolean alwaysFold;
    @Property
    private Boolean addBlankLine;

    /**
     * The `flavorTemplate` specifies the format used for replacing strings with their i18n (internationalization) counterparts.
     * For example:
     * In many situations, the default representation for i18n follows the `$i18n.t('key')` pattern. However, this can vary depending on
     * the specific framework or developers' preferences for handling i18n. The ability to dynamically change this template adds flexibility and customization
     * to cater to different i18n handling methods.
     */
    @Property
    private String flavorTemplate;

    @Property
    private NamingConvention caseFormat;

    public ProjectSettingsState() {
        this(new DefaultPreset());
    }

    public ProjectSettingsState(ProjectSettings defaults) {
        // Apply defaults on initialization
        this.localesDirectory = defaults.getLocalesDirectory();
        this.folderStrategy = defaults.getFolderStrategy();
        this.parserStrategy = defaults.getParserStrategy();
        this.filePattern = defaults.getFilePattern();

        this.includeSubDirs = defaults.isIncludeSubDirs();
        this.sorting = defaults.isSorting();

        this.namespaceDelimiter = defaults.getNamespaceDelimiter();
        this.sectionDelimiter = defaults.getSectionDelimiter();
        this.contextDelimiter = defaults.getContextDelimiter();
        this.pluralDelimiter = defaults.getPluralDelimiter();
        this.defaultNamespace = defaults.getDefaultNamespace();
        this.previewLocale = defaults.getPreviewLocale();

        this.nestedKeys = defaults.isNestedKeys();
        this.assistance = defaults.isAssistance();

        this.alwaysFold = defaults.isAlwaysFold();
        this.addBlankLine = defaults.isAddBlankLine();
        this.flavorTemplate = defaults.getFlavorTemplate();
        this.caseFormat = defaults.getCaseFormat();
    }

    @Override
    public @Nullable String getLocalesDirectory() {
        return localesDirectory;
    }

    @Override
    public @NotNull FolderStrategyType getFolderStrategy() {
        return folderStrategy;
    }

    @Override
    public @NotNull ParserStrategyType getParserStrategy() {
        return parserStrategy;
    }

    @Override
    public @NotNull String getFilePattern() {
        return filePattern;
    }

    @Override
    public boolean isIncludeSubDirs() {
        return includeSubDirs;
    }

    @Override
    public boolean isSorting() {
        return sorting;
    }

    @Override
    public @Nullable String getNamespaceDelimiter() {
        return namespaceDelimiter;
    }

    @Override
    public @NotNull String getSectionDelimiter() {
        return sectionDelimiter;
    }

    @Override
    public @Nullable String getContextDelimiter() {
        return contextDelimiter;
    }

    @Override
    public @Nullable String getPluralDelimiter() {
        return pluralDelimiter;
    }

    @Nullable
    @Override
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    @Override
    public @NotNull String getPreviewLocale() {
        return previewLocale;
    }

    @Override
    public boolean isNestedKeys() {
        return nestedKeys;
    }

    @Override
    public boolean isAssistance() {
        return assistance;
    }

    @Override
    public boolean isAlwaysFold() {
        return alwaysFold;
    }

    @Override
    public boolean isAddBlankLine() { return addBlankLine;}

    @Override
    public String getFlavorTemplate() {
        return this.flavorTemplate;
    }

    @Override
    public @NotNull NamingConvention getCaseFormat() {
        return this.caseFormat;
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

    public void setIncludeSubDirs(Boolean includeSubDirs) {
        this.includeSubDirs = includeSubDirs;
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

    public void setAlwaysFold(Boolean alwaysFold) {
        this.alwaysFold = alwaysFold;
    }

    public void setAddBlankLine(Boolean addBlankLine) { this.addBlankLine = addBlankLine; }

    public void setFlavorTemplate(String flavorTemplate) {
        this.flavorTemplate = flavorTemplate;
    }

    public void setCaseFormat(NamingConvention caseFormat) {
        this.caseFormat = caseFormat;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectSettingsState that = (ProjectSettingsState) o;
        return sorting == that.sorting
                && folderStrategy == that.folderStrategy
                && parserStrategy == that.parserStrategy
                && addBlankLine == that.addBlankLine
                && Objects.equals(localesDirectory, that.localesDirectory)
                && Objects.equals(filePattern, that.filePattern)
                && Objects.equals(includeSubDirs, that.includeSubDirs)
                && Objects.equals(namespaceDelimiter, that.namespaceDelimiter)
                && Objects.equals(sectionDelimiter, that.sectionDelimiter)
                && Objects.equals(contextDelimiter, that.contextDelimiter)
                && Objects.equals(pluralDelimiter, that.pluralDelimiter)
                && Objects.equals(defaultNamespace, that.defaultNamespace)
                && Objects.equals(previewLocale, that.previewLocale)
                && Objects.equals(nestedKeys, that.nestedKeys)
                && Objects.equals(assistance, that.assistance)
                && Objects.equals(alwaysFold, that.alwaysFold)
                && Objects.equals(flavorTemplate, that.flavorTemplate)
                && Objects.equals(caseFormat, that.caseFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                localesDirectory, folderStrategy, parserStrategy, filePattern, includeSubDirs,
                sorting, namespaceDelimiter, sectionDelimiter, contextDelimiter, pluralDelimiter,
                defaultNamespace, previewLocale, nestedKeys, assistance, alwaysFold, addBlankLine, flavorTemplate, caseFormat
        );
    }

    @Override
    public String toString() {
        return "ProjectSettingsState{" +
                "localesDirectory='" + localesDirectory + '\'' +
                ", folderStrategy=" + folderStrategy +
                ", parserStrategy=" + parserStrategy +
                ", filePattern='" + filePattern + '\'' +
                ", includeSubDirs=" + includeSubDirs +
                ", sorting=" + sorting +
                ", namespaceDelimiter='" + namespaceDelimiter + '\'' +
                ", sectionDelimiter='" + sectionDelimiter + '\'' +
                ", contextDelimiter='" + contextDelimiter + '\'' +
                ", pluralDelimiter='" + pluralDelimiter + '\'' +
                ", defaultNamespace='" + defaultNamespace + '\'' +
                ", previewLocale='" + previewLocale + '\'' +
                ", nestedKeys=" + nestedKeys +
                ", assistance=" + assistance +
                ", alwaysFold=" + alwaysFold +
                ", addBlankLine=" + addBlankLine +
                ", flavorTemplate=" + flavorTemplate +
                ", caseFormat=" + caseFormat.toString() +
                '}';
    }
}
