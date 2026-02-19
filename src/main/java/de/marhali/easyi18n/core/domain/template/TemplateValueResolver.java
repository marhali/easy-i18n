package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Template value resolver.
 *
 * @author marhali
 */
public interface TemplateValueResolver {
    /**
     * Resolves the provided input against the underlying template.
     *
     * @param input Input to match against
     * @return {@code null} if input does not match, otherwise with resolved {@link I18nParams parameters}.
     */
    @Nullable I18nParams resolve(@NotNull String input);
}
