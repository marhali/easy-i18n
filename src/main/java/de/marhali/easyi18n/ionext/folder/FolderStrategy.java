package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationFile;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a specific translation file directory structure.
 * @author marhali
 */
public abstract class FolderStrategy {

    protected final @NotNull SettingsState settings;

    public FolderStrategy(@NotNull SettingsState settings) {
        this.settings = settings;
    }

    /**
     * Searches the translation folder for matching files based on the implementing strategy.
     * The provided directory is already checked as a directory and can be used to query child items.
     * @param localesDirectory Configured translation file directory
     * @return translation files which matches the strategy
     */
    public abstract List<TranslationFile> findFiles(@NotNull VirtualFile localesDirectory);

    /**
     * Checks if the provided file is not a directory and matches the configured file pattern
     * @param file File to check
     * @return true if file matches and should be processed
     */
    protected boolean isFileRelevant(@NotNull VirtualFile file) {
        return !file.isDirectory() && FilenameUtils.wildcardMatch(file.getName(), this.settings.getFilePattern());
    }
}
