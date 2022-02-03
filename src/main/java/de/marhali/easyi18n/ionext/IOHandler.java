package de.marhali.easyi18n.ionext;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.ionext.folder.FolderStrategy;
import de.marhali.easyi18n.ionext.parser.ParserStrategy;
import de.marhali.easyi18n.ionext.parser.ParserStrategyType;
import de.marhali.easyi18n.model.*;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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

        Logger.getInstance(IOHandler.class).debug("Using: ",
                settings.getFolderStrategy(), settings.getParserStrategy(), settings.getFilePattern());
    }

    /**
     * Reads translation files from the local project into our data structure. <br>
     * <b>Note:</b> This method needs to be called from a Read-Action-Context (see ApplicationManager)
     * @return Translation data based on the configured strategies
     * @throws Exception Could not read translation data
     */
    public @NotNull TranslationData read() throws Exception {
        String localesPath = this.settings.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) {
            throw new IllegalArgumentException("Locales path must not be empty");
        }

        VirtualFile localesDirectory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(localesDirectory == null || !localesDirectory.isDirectory()) {
            throw new IllegalArgumentException("Specified locales path is invalid (" + localesPath + ")");
        }

        TranslationData data = new TranslationData(this.settings.isSortKeys());
        List<TranslationFile> translationFiles = this.folderStrategy.analyzeFolderStructure(localesDirectory);

        for(TranslationFile file : translationFiles) {
            this.parserStrategy.read(file, data);
        }

        return data;
    }

    /**
     * Writes the provided translation data to the local project files <br>
     * <b>Note:</b> This method must be called from an Write-Action-Context (see ApplicationManager)
     * @param data Cached translation data to save
     * @throws Exception Write action failed
     */
    public void write(@NotNull TranslationData data) throws Exception {
        String localesPath = this.settings.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) {
            throw new IllegalArgumentException("Locales path must not be empty");
        }

        List<TranslationFile> translationFiles =
                this.folderStrategy.constructFolderStructure(localesPath, this.parserStrategyType, data);

        for(TranslationFile file : translationFiles) {
            this.parserStrategy.write(data, file);
        }
    }
}
