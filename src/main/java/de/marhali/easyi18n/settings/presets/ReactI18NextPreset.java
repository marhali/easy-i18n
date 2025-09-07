package de.marhali.easyi18n.settings.presets;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Preset for React - i18n-next
 * @author marhali
 */
public class ReactI18NextPreset implements ProjectSettings {
    @Override
    public @Nullable String getLocalesDirectory() {
        return null;
    }

    @Override
    public @NotNull FolderStrategyType getFolderStrategy() {
        return FolderStrategyType.MODULARIZED_NAMESPACE;
    }

    @Override
    public @NotNull ParserStrategyType getParserStrategy() {
        return ParserStrategyType.JSON;
    }

    @Override
    public @NotNull String getFilePattern() {
        return "*.json";
    }

    @Override
    public boolean isIncludeSubDirs() {
        return false;
    }

    @Override
    public boolean isSorting() {
        return true;
    }

    @Override
    public @Nullable String getNamespaceDelimiter() {
        return ":";
    }

    @Override
    public @NotNull String getSectionDelimiter() {
        return ".";
    }

    @Override
    public @Nullable String getContextDelimiter() {
        return "_";
    }

    @Override
    public @Nullable String getPluralDelimiter() {
        return "_";
    }

    @Override
    public @Nullable String getDefaultNamespace() {
        return "common";
    }

    @Override
    public @NotNull String getPreviewLocale() {
        return "en";
    }

    @Override
    public boolean isNestedKeys() {
        return false;
    }

    @Override
    public boolean isAssistance() {
        return true;
    }

    @Override
    public boolean isAlwaysFold() {
        return false;
    }

    @Override
    public boolean isAddBlankLine() {return false; }

    @Override
    public String getFlavorTemplate() {
        return "$i18n.t";
    }
    @Override
    public @NotNull NamingConvention getCaseFormat() {
        return NamingConvention.CAMEL_CASE;
    }
}
