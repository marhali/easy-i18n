package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.ionext.parser.ParserStrategyType;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public abstract @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory);

    /**
     * Analyzes the provided translation data and returns the directory structure based on the implementing strategy
     * @param localesPath Configured locales path
     * @param data Translation data to use for write action
     * @return translation file structure
     */
    public abstract @NotNull List<TranslationFile> constructFolderStructure(@NotNull String localesPath,
            @NotNull ParserStrategyType type, @NotNull TranslationData data) throws IOException;

    /**
     * Checks if the provided file is not a directory and matches the configured file pattern
     * @param file File to check
     * @return true if file matches and should be processed
     */
    protected boolean isFileRelevant(@NotNull VirtualFile file) {
        return !file.isDirectory() && FilenameUtils.wildcardMatch(file.getName(), this.settings.getFilePattern());
    }

    /**
     *
     * @param parent Directory path
     * @param child File name with extension
     * @return IntelliJ {@link VirtualFile}
     * @throws IOException Could not access file
     */
    protected @NotNull VirtualFile constructFile(@NotNull String parent, @NotNull String child) throws IOException {
        File file = new File(parent, child);
        boolean exists = file.createNewFile();

        VirtualFile vf = exists
                ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                : LocalFileSystem.getInstance().findFileByIoFile(file);

        return Objects.requireNonNull(vf);
    }
}
