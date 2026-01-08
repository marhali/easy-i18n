package de.marhali.easyi18n.core.application;

import de.marhali.easyi18n.core.application.cqrs.Command;
import de.marhali.easyi18n.core.application.cqrs.CommandDispatcher;
import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.application.cqrs.QueryDispatcher;
import de.marhali.easyi18n.core.domain.model.ProjectId;
import org.jetbrains.annotations.NotNull;

public final class I18nApplication {

    private final CommandDispatcher commands = new CommandDispatcher();
    private final QueryDispatcher queries = new QueryDispatcher();

    public I18nApplication() {
        // TODO: ...

        registerHandlers();
    }

    public void command(@NotNull ProjectId projectId, Command command) {
        commands.dispatch(projectId, command);
    }

    public <R> @NotNull R query(@NotNull ProjectId projectId, @NotNull Query<R> query) {
        return queries.dispatch(projectId, query);
    }

    private void registerHandlers() {}
}
