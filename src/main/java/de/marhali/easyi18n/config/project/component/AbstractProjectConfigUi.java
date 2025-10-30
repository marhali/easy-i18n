package de.marhali.easyi18n.config.project.component;

import com.intellij.openapi.project.Project;
import com.intellij.util.ui.FormBuilder;
import de.marhali.easyi18n.config.ConfigComponent;

import java.util.ResourceBundle;

/**
 * @author marhali
 * @param <State> State that the implementing component represents in UI.
 */
public abstract class AbstractProjectConfigUi<State> implements ConfigComponent<State, FormBuilder> {
    protected final static ResourceBundle i18n = ResourceBundle.getBundle("i18n_config");

    protected final Project project;

    protected State state;

    protected AbstractProjectConfigUi(Project project) {
        this.project = project;
    }

    /**
     * Every component needs to override this function, call this super method and sync every UI component.
     * @param state New state to apply
     */
    @Override
    public void applyStateToComponent(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }
}
