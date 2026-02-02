package de.marhali.easyi18n.core.application;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

/**
 * Entry point to interact with the domain-level application.
 * Orchestrates and dispatches commands and queries based on the CQRS principle.
 *
 * @author marhali
 */
public final class I18nApplication {

    private final CommandDispatcher commands;
    private final QueryDispatcher queries;

    public I18nApplication(@NotNull CommandDispatcher commands, @NotNull QueryDispatcher queries) {
        this.commands = commands;
        this.queries = queries;
    }

    public void command(@NotNull ProjectId projectId, Command command) {
        commands.dispatch(projectId, command);
    }

    public <R> @NotNull R query(@NotNull ProjectId projectId, @NotNull Query<R> query) {
        return queries.dispatch(projectId, query);
    }
}
