package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Domain-level project identifier.
 *
 * @param value Project id
 *
 * @author marhali
 */
public record ProjectId(
    @NotNull String value
) implements Comparable<ProjectId> {
    @Override
    public int compareTo(@NotNull ProjectId o) {
        return this.value.compareTo(o.value);
    }
}
