package de.marhali.easyi18n.settings;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API to access the project-specific configuration for this plugin.
 * @author marhaliu
 */
public interface ProjectSettings {
    // Resource Configuration
    @Nullable String getLocalesDirectory();
    @NotNull FolderStrategyType getFolderStrategy();
    @NotNull ParserStrategyType getParserStrategy();
    @NotNull String getFilePattern();

    boolean isSorting();

    // Editor Configuration
    @Nullable String getNamespaceDelimiter();
    @NotNull String getSectionDelimiter();
    @Nullable String getContextDelimiter();
    @Nullable String getPluralDelimiter();
    @Nullable String getDefaultNamespace();
    @NotNull String getPreviewLocale();

    boolean isNestedKeys();
    boolean isAssistance();
}
