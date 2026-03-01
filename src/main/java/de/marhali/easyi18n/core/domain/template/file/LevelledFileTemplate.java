package de.marhali.easyi18n.core.domain.template.file;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.template.TemplateElement;
import de.marhali.easyi18n.core.domain.template.TemplateValue;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Levelled translation file content template.
 *
 * @author marhali
 */
public interface LevelledFileTemplate {
    /**
     * Parses all params from the current file level
     * @param canonical Raw text at this file level
     * @return {@link I18nParams}
     */
    @NotNull I18nParams fromCanonical(@NotNull String canonical);

    /**
     * Builds the canonical text for this file level.
     * @param params Params
     * @return Set of built {@link TemplateValue}'s
     */
    @NotNull Set<@NotNull TemplateValue> buildVariants(@NotNull I18nParams params);

    /**
     * @return Set of needed parameters to build this file level
     */
    @NotNull Set<TemplateElement.@NotNull Placeholder> getNeededParameters();
}
