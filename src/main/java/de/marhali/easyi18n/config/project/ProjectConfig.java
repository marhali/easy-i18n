package de.marhali.easyi18n.config.project;

import de.marhali.easyi18n.config.project.preset.ProjectConfigPresetDefault;
import de.marhali.easyi18n.ionext.file.FileParser;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project-level configuration state.
 *
 * @author marhali
 */
public class ProjectConfig {

    /**
     * Default configuration. Derived from {@link ProjectConfigPresetDefault default} preset.
     */
    public static ProjectConfig fromDefaultPreset() {
        return new ProjectConfigPresetDefault().applyPreset(null);
    }

    // Common configuration
    /**
     * Indicates whether editor code assistance should be enabled or not.
     */
    private boolean editorAssistance;

    /**
     * Indicates whether translation keys should be sorted alphabetically.
     */
    private boolean sorting;

    /**
     * Defines the locale to be used for development and preview.
     */
    private String previewLocale;

    // Modules configuration
    /**
     * Configured modules.
     */
    private List<ProjectConfigModule> modules;

    // File extension mapper
    /**
     * Maps file parser with file extensions.
     */
    private Map<FileParser, List<String>> fileExtMapper;

    /**
     * Creates a deep copy from the provided {@link ProjectConfig config}.
     * @param origin Config to copy from
     */
    public ProjectConfig(ProjectConfig origin) {
        this.editorAssistance = origin.editorAssistance;
        this.sorting = origin.sorting;
        this.previewLocale = origin.previewLocale;
        this.modules = new ArrayList<>(origin.modules.stream().map(ProjectConfigModule::new).toList());
        this.fileExtMapper = new HashMap<>(origin.fileExtMapper.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, value -> new ArrayList<>(value.getValue()))));
    }

    public ProjectConfig() {
    }

    public boolean isEditorAssistance() {
        return editorAssistance;
    }

    public void setEditorAssistance(boolean editorAssistance) {
        this.editorAssistance = editorAssistance;
    }

    public boolean isSorting() {
        return sorting;
    }

    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }

    public String getPreviewLocale() {
        return previewLocale;
    }

    public void setPreviewLocale(String previewLocale) {
        this.previewLocale = previewLocale;
    }

    public List<ProjectConfigModule> getModules() {
        return modules;
    }

    public void setModules(List<ProjectConfigModule> modules) {
        this.modules = modules;
    }

    public Map<FileParser, List<String>> getFileExtMapper() {
        return fileExtMapper;
    }

    public void setFileExtMapper(Map<FileParser, List<String>> fileExtMapper) {
        this.fileExtMapper = fileExtMapper;
    }

    @Override
    public String toString() {
        return "ProjectConfig{" +
            "editorAssistance=" + editorAssistance +
            ", sorting=" + sorting +
            ", previewLocale='" + previewLocale + '\'' +
            ", modules=" + modules +
            ", fileExtMapper=" + fileExtMapper +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProjectConfig that = (ProjectConfig) o;
        return editorAssistance == that.editorAssistance && sorting == that.sorting && Objects.equals(previewLocale, that.previewLocale) && Objects.equals(modules, that.modules) && Objects.equals(fileExtMapper, that.fileExtMapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(editorAssistance, sorting, previewLocale, modules, fileExtMapper);
    }
}
