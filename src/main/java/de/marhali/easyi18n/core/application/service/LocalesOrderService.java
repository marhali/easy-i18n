package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Server to order locales by configured preview locale.
 *
 * @author marhali
 */
public class LocalesOrderService {

    private final @NotNull ProjectConfigPort projectConfigPort;

    public LocalesOrderService(@NotNull ProjectConfigPort projectConfigPort) {
        this.projectConfigPort = projectConfigPort;
    }

    /**
     * Orders the provided set of locales by the configured preview locale.
     * @param locales Given locales
     * @return Ordered locales by preview locale
     */
    public @NotNull LinkedHashSet<@NotNull LocaleId> orderByPreviewLocale(@NotNull Set<@NotNull LocaleId> locales) {
        LinkedHashSet<LocaleId> orderedLocales = new LinkedHashSet<>(locales);
        LocaleId previewLocale = projectConfigPort.read().previewLocale();

        if (orderedLocales.contains(previewLocale)) {
            orderedLocales.remove(previewLocale);
            orderedLocales.addFirst(previewLocale);
        }

        return orderedLocales;
    }
}
