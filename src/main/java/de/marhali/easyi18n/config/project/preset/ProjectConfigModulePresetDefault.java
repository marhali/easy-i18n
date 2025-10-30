package de.marhali.easyi18n.config.project.preset;

import de.marhali.easyi18n.config.project.KeyNamingConvention;
import de.marhali.easyi18n.config.project.ProjectConfigModule;

public class ProjectConfigModulePresetDefault implements ProjectConfigModulePresetProvider{
    @Override
    public ProjectConfigModule applyPreset(ProjectConfigModule unusedNullablePreviousState) {
        var cfg = new ProjectConfigModule();

        cfg.setName("default");
        cfg.setFileFolderPattern("");
        cfg.setFileContentPattern("");
        cfg.setKeyPattern("");
        cfg.setRootDirectory("");
        cfg.setModuleDelimiter(";");
        cfg.setNamespaceDelimiter(":");
        cfg.setSectionDelimiter(".");
        cfg.setDefaultNamespace("");
        cfg.setI18nTemplate("$i18n.t");
        cfg.setKeyNamingConvention(KeyNamingConvention.CAMEL_CASE);

        return cfg;
    }
}
