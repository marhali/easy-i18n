package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ProjectId;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Factory that picks the desired {@link Map} implementation based on whether the keys should be sorted or not.
 *
 * @author marhali
 */
public record MapImplFactory(
    boolean sort
) {
    public MapImplFactory(@NotNull ProjectId projectId, @NotNull ProjectConfigPort projectConfigPort) {
        this(projectConfigPort.read(projectId).sorting());
    }

    public @NotNull <K extends Comparable<K>, V> Map<@NotNull K, @NotNull V> get() {
        return sort ? new TreeMap<>() : new LinkedHashMap<>();
    }
}
