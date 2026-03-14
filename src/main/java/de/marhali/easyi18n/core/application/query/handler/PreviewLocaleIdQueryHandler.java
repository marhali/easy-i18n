package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.application.cqrs.QueryHandler;
import de.marhali.easyi18n.core.application.query.PreviewLocaleIdQuery;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

/**
 * Query handler for {@link PreviewLocaleIdQuery}.
 *
 * @author marhali
 */
public class PreviewLocaleIdQueryHandler implements QueryHandler<PreviewLocaleIdQuery, LocaleId> {

    private final @NotNull ProjectConfigPort projectConfigPort;

    public PreviewLocaleIdQueryHandler(@NotNull ProjectConfigPort projectConfigPort) {
        this.projectConfigPort = projectConfigPort;
    }

    @Override
    public @NotNull LocaleId handle(@NotNull PreviewLocaleIdQuery query) {
        return projectConfigPort.read().previewLocale();
    }
}
