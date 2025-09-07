package de.marhali.easyi18n.settings;

import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;

import de.marhali.easyi18n.settings.presets.NamingConvention;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Settings preset to test the functionality of the settings service.
 *
 * @author marhali
 */
public class SettingsTestPreset implements ProjectSettings {
    @Override
    public @Nullable String getLocalesDirectory() {
        return "myCustomLocalesDirectory";
    }

    @Override
    public @NotNull FolderStrategyType getFolderStrategy() {
        return FolderStrategyType.MODULARIZED_NAMESPACE;
    }

    @Override
    public @NotNull ParserStrategyType getParserStrategy() {
        return ParserStrategyType.JSON5;
    }

    @Override
    public @NotNull String getFilePattern() {
        return "*.testfile.json5";
    }

    @Override
    public boolean isIncludeSubDirs() {
        return true;
    }

    @Override
    public boolean isSorting() {
        return false;
    }

    @Override
    public @Nullable String getNamespaceDelimiter() {
        return "nsDelim";
    }

    @Override
    public @NotNull String getSectionDelimiter() {
        return "sctDelim";
    }

    @Override
    public @Nullable String getContextDelimiter() {
        return "ctxDelim";
    }

    @Override
    public @Nullable String getPluralDelimiter() {
        return "plDelim";
    }

    @Override
    public @Nullable String getDefaultNamespace() {
        return "defNs";
    }

    @Override
    public @NotNull String getPreviewLocale() {
        return "prevLocale";
    }

    @Override
    public boolean isNestedKeys() {
        return true;
    }

    @Override
    public boolean isAssistance() {
        return false;
    }

    @Override
    public boolean isAlwaysFold() {
        return false;
    }

    @Override
    public boolean isAddBlankLine() { return false; }

    @Override
    public String getFlavorTemplate() {
        return "t";
    }

    @Override
    public @NotNull NamingConvention getCaseFormat() {
        return NamingConvention.CAMEL_CASE;
    }
}
