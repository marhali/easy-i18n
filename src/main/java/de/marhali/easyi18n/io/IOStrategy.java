package de.marhali.easyi18n.io;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Primary interface for the exchange of translation data with the underlying IO system.
 * The selection of the right IO strategy is done by the @canUse method (first match).
 * Every strategy needs to be registered inside {@link de.marhali.easyi18n.DataStore}
 *
 * @author marhali
 */
public interface IOStrategy {
    /**
     * Decides whether this strategy should be applied or not. First matching one will be used.
     * @param project IntelliJ project context
     * @param localesPath Root directory which leads to all i18n files
     * @param state Plugin configuration
     * @return true if strategy is responsible for the found structure
     */
    boolean canUse(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state);

    /**
     * Loads the translation files and passes them in the result consumer.
     * Result payload might be null if operation failed.
     * @param project IntelliJ project context
     * @param localesPath Root directory which leads to all i18n files
     * @param state Plugin configuration
     * @param result Passes loaded data
     */
    void read(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state,
              @NotNull Consumer<@Nullable TranslationData> result);

    /**
     * Writes the provided translation data to the IO system.
     * @param project InteliJ project context
     * @param localesPath Root directory which leads to all i18n files
     * @param state Plugin configuration
     * @param data Translations to save
     * @param result Indicates whether the operation was successful
     */
    void write(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state,
               @NotNull TranslationData data, @NotNull Consumer<Boolean> result);

    /**
     * Checks if the provided file should be processed for translation data
     * @param state Plugin configuration
     * @param file File to check
     * @return true if file matches pattern
     */
    default boolean isFileRelevant(@NotNull SettingsState state, @NotNull VirtualFile file) {
        System.out.println(file.getName() + " " + FilenameUtils.wildcardMatch(file.getName(), state.getFilePattern()));
        return FilenameUtils.wildcardMatch(file.getName(), state.getFilePattern());
    }
}
