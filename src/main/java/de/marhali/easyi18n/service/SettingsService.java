package de.marhali.easyi18n.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.model.SettingsState;

import org.jetbrains.annotations.NotNull;

/**
 * Persistent settings storage at project level.
 * @author marhali
 */
@State(name = "EasyI18nSettings")
@Deprecated
public class SettingsService implements PersistentStateComponent<SettingsState> {

    public static SettingsService getInstance(Project project) {
        return project.getService(SettingsService.class);
    }

    private SettingsState state;

    public SettingsService() {
        this.state = new SettingsState();
    }

    @Override
    public @NotNull SettingsState getState() {
        return state;
    }

    public void setState(SettingsState state) {
        this.state = state;
    }

    @Override
    public void loadState(@NotNull SettingsState state) {
        this.state = state;
    }
}
