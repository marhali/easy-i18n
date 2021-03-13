package de.marhali.easyi18n.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author marhali
 */
public class SettingsState {

    public static final String DEFAULT_PREVIEW_LOCALE = "en";

    private String localesPath;
    private String previewLocale;

    public SettingsState() {}

    public @Nullable String getLocalesPath() {
        return localesPath;
    }

    public void setLocalesPath(String localesPath) {
        this.localesPath = localesPath;
    }

    public @NotNull String getPreviewLocale() {
        return previewLocale != null ? previewLocale : DEFAULT_PREVIEW_LOCALE;
    }

    public void setPreviewLocale(String previewLocale) {
        this.previewLocale = previewLocale;
    }
}