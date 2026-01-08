package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;

/**
 * Module identifier within a project.
 *
 * @param name A descriptive name that identifies this resource configuration.
 *
 * @author marhali
 */
public record ModuleId(
    @NotNull String name
) {
}
