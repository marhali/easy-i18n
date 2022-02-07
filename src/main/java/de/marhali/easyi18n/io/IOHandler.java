package de.marhali.easyi18n.io;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.exception.EmptyLocalesDirException;
import de.marhali.easyi18n.io.folder.FolderStrategy;
import de.marhali.easyi18n.io.parser.ParserStrategy;
import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.model.*;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Central component for IO operations based on the configured strategies.
 * @author marhali
 */
public class IOHandler {

    private final @NotNull SettingsState settings;

    private final @NotNull FolderStrategy folderStrategy;

    private final @NotNull ParserStrategyType parserStrategyType;
    private final @NotNull ParserStrategy parserStrategy;

    public IOHandler(@NotNull SettingsState settings) throws Exception {

        this.settings = settings;

        this.folderStrategy = settings.getFolderStrategy().getStrategy()
                .getDeclaredConstructor(SettingsState.class).newInstance(settings);

        this.parserStrategyType = settings.getParserStrategy();
        this.parserStrategy = parserStrategyType.getStrategy()
                .getDeclaredConstructor(SettingsState.class).newInstance(settings);
    }

    /**
     * Reads translation files from the local project into our data structure. <br>
     * <b>Note:</b> This method needs to be called from a Read-Action-Context (see ApplicationManager)
     * @return Translation data based on the configured strategies
     * @throws IOException Could not read translation data
     */
    public @NotNull TranslationData read() throws IOException {
        String localesPath = this.settings.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) {
            throw new EmptyLocalesDirException("Locales path must not be empty");
        }

        VirtualFile localesDirectory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(localesDirectory == null || !localesDirectory.isDirectory()) {
            throw new IllegalArgumentException("Specified locales path is invalid (" + localesPath + ")");
        }

        TranslationData data = new TranslationData(this.settings.isSortKeys());
        List<TranslationFile> translationFiles = this.folderStrategy.analyzeFolderStructure(localesDirectory);

        for(TranslationFile file : translationFiles) {
            try {
                this.parserStrategy.read(file, data);
            } catch(Exception ex) {
                throw new IOException(file + "\n\n" + ex.getMessage(), ex);
            }
        }

        return data;
    }

    /**
     * Writes the provided translation data to the local project files <br>
     * <b>Note:</b> This method must be called from an Write-Action-Context (see ApplicationManager)
     * @param data Cached translation data to save
     * @throws IOException Write action failed
     */
    public void write(@NotNull TranslationData data) throws IOException {
        String localesPath = this.settings.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) {
            throw new EmptyLocalesDirException("Locales path must not be empty");
        }

        List<TranslationFile> translationFiles =
                this.folderStrategy.constructFolderStructure(localesPath, this.parserStrategyType, data);

        for(TranslationFile file : translationFiles) {
            try {
                this.parserStrategy.write(data, file);
            } catch (Exception ex) {
                throw new IOException(file + "\n\n" + ex.getMessage(), ex);
            }
        }
    }
}
