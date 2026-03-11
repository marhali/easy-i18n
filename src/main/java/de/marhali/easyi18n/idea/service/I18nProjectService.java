package de.marhali.easyi18n.idea.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.application.I18nApplication;
import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.idea.wiring.CoreWiring;
import org.jetbrains.annotations.NotNull;

/**
 * Project-specific service which holds a reference to the domain-level {@link I18nApplication}.
 * Provides convenience methods to process commands and queries.
 *
 * @author marhali
 */
@Service(Service.Level.PROJECT)
public final class I18nProjectService implements Disposable {

    private final @NotNull I18nApplication application;

    public I18nProjectService(@NotNull Project project) {
        this.application = CoreWiring.create(project, this);
    }

    /**
     * @see I18nApplication#command(Command)
     */
    public void command(@NotNull Command command) {
        application.command(command);
    }

    /**
     * @see I18nApplication#query(Query)
     */
    public <R> @NotNull R query(@NotNull Query<R> query) {
        return application.query(query);
    }

    @Override
    public void dispose() {
        // Implements Disposable as root disposable within the plugin
        // Elements that needs a parent disposable over the entire lifespan of the plugin should use this disposable
    }
}
