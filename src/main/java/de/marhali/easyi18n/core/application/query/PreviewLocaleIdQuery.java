package de.marhali.easyi18n.core.application.query;

import de.marhali.easyi18n.core.application.cqrs.Query;
import de.marhali.easyi18n.core.domain.model.LocaleId;

/**
 * Query to retrieve the user-configured preview locale identifier.
 *
 * @author marhali
 */
public record PreviewLocaleIdQuery() implements Query<LocaleId> { }
