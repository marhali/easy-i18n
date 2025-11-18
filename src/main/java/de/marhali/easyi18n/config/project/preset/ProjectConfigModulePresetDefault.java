package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.KeyNamingConvention;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

public class ProjectConfigModulePresetDefault implements ProjectConfigModulePresetProvider{
    @Override
    public ProjectConfigModule applyPreset(ProjectConfigModule unusedNullablePreviousState) {
        // Default preset must use new instance
        var cfg = new ProjectConfigModule();

        cfg.setName("default");
        cfg.setPathTemplate("");
        cfg.setFileTemplate("");
        cfg.setKeyTemplate("");
        cfg.setRootDirectory("");
        cfg.setDefaultNamespace("");
        cfg.setI18nTemplate("$i18n.t");
        cfg.setKeyNamingConvention(KeyNamingConvention.CAMEL_CASE);

        return cfg;
    }
}
