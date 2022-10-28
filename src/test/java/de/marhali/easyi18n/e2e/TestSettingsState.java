package de.marhali.easyi18n.e2e;

import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.settings.presets.DefaultPreset;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Settings base for end-to-end tests.
 * @author marhali
 */
public class TestSettingsState extends DefaultPreset {

    private final String localesDirectory;
    private final FolderStrategyType folderStrategy;
    private final ParserStrategyType parserStrategy;

    public TestSettingsState(String localesDirectory, FolderStrategyType folderStrategy, ParserStrategyType parserStrategy) {
        this.localesDirectory = localesDirectory;
        this.folderStrategy = folderStrategy;
        this.parserStrategy = parserStrategy;
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
        return "*.*";
    }

    @Override
    public boolean isSorting() {
        return false;
    }
}
