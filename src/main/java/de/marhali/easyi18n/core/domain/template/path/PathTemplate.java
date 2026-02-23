package de.marhali.easyi18n.core.domain.template.path;

import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Translation file path template.
 *
 * @author marhali
 */
public interface PathTemplate {
    /**
     * Converts the given {@link I18nPath} to its canonical file path representation.
     * @param path {@link I18nPath}
     * @return Canonical translation file path
     */
    @NotNull String toCanonical(@NotNull I18nPath path);

    /**
     * Parses a {@link I18nPath} from a canonical translation file path.
     * @param canonical Canonical translation file path
     * @return {@link I18nPath}
     */
    @NotNull I18nPath fromCanonical(@NotNull String canonical);

    /**
     * Matches {@link I18nParams} from a canonical translation file path
     * @param canonical Canonical translation file path
     * @return Matched {@link I18nParams} if canonical file path could be matched against the template, otherwise {@code null}
     */
    @Nullable I18nParams matchCanonical(@NotNull String canonical);

    /**
     * Builds all possible {@link I18nPath}'s for the specified parameters.
     * @param params {@link I18nParams}
     * @return Set of all built variants
     */
    @NotNull Set<@NotNull I18nPath> buildVariants(@NotNull I18nParams params);

    /**
     * Retrieves the translation file extension (e.g. {@code json}, {@code yaml}).
     * @return File extension specified by template
     */
    @NotNull String getFileExtension();

    /**
     * Retrieves the most common parent path for the template definition.
     * In simpler terms, this will build the template until a placeholder parameter is reached.
     * @return Parent path as string
     */
    @NotNull String getMostCommonParentPath();
}
