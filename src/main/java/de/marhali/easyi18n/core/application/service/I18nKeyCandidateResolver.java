package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.application.state.I18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Resolves {@link I18nKeyCandidate}'s against the underlying {@link I18nStore}
 * with support for {@link I18nKeyPrefix}'es.
 *
 * @author marhali
 */
public class I18nKeyCandidateResolver {

    private final @NotNull ProjectConfigPort projectConfigPort;
    private final @NotNull I18nStore store;

    public I18nKeyCandidateResolver(@NotNull ProjectConfigPort projectConfigPort, @NotNull I18nStore store) {
        this.projectConfigPort = projectConfigPort;
        this.store = store;
    }

    public @Nullable I18nEntry resolveExact(@NotNull ModuleId moduleId, @NotNull I18nKeyCandidate keyCandidate) {
        I18nModule moduleStore = store.getSnapshot().getModuleOrThrow(moduleId);

        for (I18nKey key : constructKeys(moduleId, keyCandidate)) {
            if (moduleStore.hasTranslation(key)) {
                // First match will resolve
                return new I18nEntry(key, Objects.requireNonNull(moduleStore.getTranslation(key)));
            }
        }

        return null;
    }

    private @NotNull Set<@NotNull I18nKey> constructKeys(@NotNull ModuleId moduleId, @NotNull I18nKeyCandidate keyCandidate) {
        var keys = new HashSet<I18nKey>();

        keys.add(I18nKey.of(keyCandidate.canonical()));

        for (I18nKeyPrefix keyPrefix : getModuleDefaultKeyPrefixes(moduleId)) {
            keys.add(keyPrefix.withCandidate(keyCandidate));
        }

        return keys;
    }

    private @NotNull Set<@NotNull I18nKeyPrefix> getModuleDefaultKeyPrefixes(@NotNull ModuleId moduleId) {
        ProjectConfig projectConfig = projectConfigPort.read();
        ProjectConfigModule moduleConfig = projectConfig.modules().get(moduleId);

        if (moduleConfig == null) {
            throw new IllegalArgumentException("Unknown module: " + moduleId);
        }

        return moduleConfig.defaultKeyPrefixes();
    }
}
