package de.marhali.easyi18n.core.domain.config.preset;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.config.KeyNamingConvention;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Default preset for {@link ProjectConfigModule}.
 *
 * @author marhali
 */
public class ProjectConfigModulePresetDefault implements PresetProvider<ProjectConfigModule> {
    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(new ModuleId("default"))
            .pathTemplate("")
            .fileCodec(FileCodec.JSON)
            .fileTemplate("")
            .keyTemplate("")
            .rootDirectory("")
            .defaultKeyPrefixes()
            .i18nTemplate("$i18n.t")
            .keyNamingConvention(KeyNamingConvention.CAMEL_CASE)
            .editorRules()
            .build();
    }
}
