package de.marhali.easyi18n;

import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.io.IOStrategy;
import de.marhali.easyi18n.io.json.JsonIOStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Responsible for loading, saving and updating translation files.
 * Provides access to the cached translation data which is used in the whole project.
 * @author marhali
 */
public class DataStore {

    private static final Set<IOStrategy> STRATEGIES = new LinkedHashSet<>(Arrays.asList(
       new JsonIOStrategy()
    ));

    private final Project project;

    private @NotNull TranslationData data;

    protected DataStore(Project project) {
        this.project = project;
        this.data = new TranslationData(true, true); // Initialize with hard-coded configuration
    }

    public @NotNull TranslationData getData() {
        return data;
    }

    /**
     * Loads the translation data into cache and overwrites any previous cached data.
     * If the configuration does not fit an empty translation instance will be populated.
     * @param successResult Consumer will inform if operation was successful
     */
    public void loadFromPersistenceLayer(@NotNull Consumer<Boolean> successResult) {
        SettingsState state = SettingsService.getInstance(this.project).getState();
        String localesPath = state.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) { // Populate empty instance
            this.data = new TranslationData(state.isSortKeys(), state.isNestedKeys());
            return;
        }

        IOStrategy strategy = this.determineStrategy(state, localesPath);

        strategy.read(this.project, localesPath, state, (data) -> {
            this.data = data == null
                    ? new TranslationData(state.isSortKeys(), state.isNestedKeys())
                    : data;

            successResult.accept(data != null);
        });
    }

    /**
     * Saves the cached translation data to the underlying io system.
     * @param successResult Consumer will inform if operation was successful
     */
    public void saveToPersistenceLayer(@NotNull Consumer<Boolean> successResult) {
        SettingsState state = SettingsService.getInstance(this.project).getState();
        String localesPath = state.getLocalesPath();

        if(localesPath == null || localesPath.isEmpty()) { // Cannot save without valid path
            successResult.accept(false);
            return;
        }

        IOStrategy strategy = this.determineStrategy(state, localesPath);

        strategy.write(this.project, localesPath, state, this.data, successResult);
    }

    /**
     * Chooses the right strategy for the opened project. An exception might be thrown on
     * runtime if the project configuration (e.g. locale files does not fit in any strategy).
     * @param state Plugin configuration
     * @param localesPath Locales directory
     * @return matching {@link IOStrategy}
     */
    public @NotNull IOStrategy determineStrategy(@NotNull SettingsState state, @NotNull String localesPath) {
        for(IOStrategy strategy : STRATEGIES) {
            if(strategy.canUse(this.project, localesPath, state)) {
                return strategy;
            }
        }

        throw new IllegalArgumentException("Could not determine i18n strategy. " +
                "At least one locale file must be defined. " +
                "For examples please visit https://github.com/marhali/easy-i18n");
    }
}