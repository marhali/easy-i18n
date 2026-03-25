package de.marhali.easyi18n.core.domain.template;

import de.marhali.easyi18n.core.domain.template.file.FileTemplate;
import de.marhali.easyi18n.core.domain.template.flavor.EditorFlavorTemplate;
import de.marhali.easyi18n.core.domain.template.key.KeyTemplate;
import de.marhali.easyi18n.core.domain.template.path.PathTemplate;
import org.jetbrains.annotations.NotNull;

/**
 * Container of all templates.
 *
 * @param path Path template
 * @param file File template
 * @param key Key template
 * @param flavor I18n flavor template
 *
 * @author marhali
 */
public record Templates(
    @NotNull PathTemplate path,
    @NotNull FileTemplate file,
    @NotNull KeyTemplate key,
    @NotNull EditorFlavorTemplate flavor
    ) {
}
