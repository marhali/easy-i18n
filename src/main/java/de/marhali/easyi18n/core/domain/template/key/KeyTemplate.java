package de.marhali.easyi18n.core.domain.template.key;

import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.template.Template;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Translation key template.
 *
 * @author marhali
 */
public interface KeyTemplate {
    /**
     * Retrieves the underlying template.
     * @return {@link Template}
     */
    @NotNull Template getTemplate();

    /**
     * Construct a {@link I18nKey} from {@link I18nParams}.
     * @param params Parameters
     * @return {@link I18nKey}
     */
    @NotNull I18nKey fromParams(@NotNull I18nParams params);

    /**
     * Resolves all {@link I18nParams} behind the given {@link I18nKey}.
     * @param key {@link I18nKey}
     * @return {@link I18nParams}
     */
    @NotNull I18nParams toParams(@NotNull I18nKey key);

    /**
     * Retrieves the key hierarchy for the specified {@link I18nKey}.
     * @param key {@link I18nKey}
     * @return List of hierarchical translation key segments
     */
    @NotNull List<@NotNull String> toHierarchy(@NotNull I18nKey key);
}
