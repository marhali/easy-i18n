package de.marhali.easyi18n.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the persistent settings which can be configured.
 * @author marhali
 */
public class SettingsState {

    public static final String DEFAULT_PREVIEW_LOCALE = "en";
    public static final String DEFAULT_FILE_PATTERN = ".*";
    public static final boolean DEFAULT_CODE_ASSISTANCE = true;

    private String localesPath;
    private String filePattern;
    private String previewLocale;
    private String prefix;
    private Boolean codeAssistance;

    public SettingsState() {}

    public @Nullable String getLocalesPath() {
        return localesPath;
    }

    public void setLocalesPath(String localesPath) {
        this.localesPath = localesPath;
    }

    public @NotNull String getFilePattern() {
        return filePattern != null ? filePattern : DEFAULT_FILE_PATTERN;
    }

    public void setFilePattern(String filePattern) {
        this.filePattern = filePattern;
    }

    public @NotNull String getPreviewLocale() {
        return previewLocale != null ? previewLocale : DEFAULT_PREVIEW_LOCALE;
    }

    public void setPreviewLocale(String previewLocale) {
        this.previewLocale = previewLocale;
    }

    public boolean isCodeAssistance() {
        return codeAssistance == null ? DEFAULT_CODE_ASSISTANCE : codeAssistance;
    }

    public void setCodeAssistance(boolean codeAssistance) {
        this.codeAssistance = codeAssistance;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}