package de.marhali.easyi18n;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.data.SettingsState;

import org.jetbrains.annotations.NotNull;

/**
 * Persistent settings storage at project level.
 * @author marhali
 */
@State(name = "EasyI18nSettings")
public class SettingsService implements PersistentStateComponent<SettingsState> {

    public static SettingsService getInstance(Project project) {
        ServiceManager.getService(project, SettingsService.class).initializeComponent();
        return ServiceManager.getService(project, SettingsService.class);
    }

    private SettingsState state;

    public SettingsService() {
        this.state = new SettingsState();
    }

    @Override
    public @NotNull SettingsState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull SettingsState state) {
        this.state = state;
    }
}
