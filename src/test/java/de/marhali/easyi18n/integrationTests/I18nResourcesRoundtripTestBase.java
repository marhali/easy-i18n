package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigBuilder;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModuleBuilder;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

import java.io.IOException;
import java.util.Set;

import static de.marhali.easyi18n.TestResourceLoader.getResourceAsString;

public abstract class I18nResourcesRoundtripTestBase {

    protected static final @NotNull ModuleId moduleId = new ModuleId("integrationTestModule");

    protected static final @NotNull ProjectConfigBuilder projectConfigBuilder = ProjectConfig.builder()
        .keyComment(false)
        .sorting(false)
        .previewLocale(new LocaleId("en"))
        .modules();

    protected static final @NotNull ProjectConfigModuleBuilder moduleConfigBuilder = ProjectConfigModule.builder()
        .id(moduleId)
        .rootDirectory("notRelevant")
        .defaultKeyPrefixes()
        .editorFlavorTemplate("notRelevant")
        .editorRules();

    protected @NotNull I18nPath provideTranslationResource(
        @NotNull IntegrationTestWiring wiring,
        @NotNull String canonicalPath,
        @NotNull String testResourcePath
    ) throws IOException {
        I18nPath path = wiring.cachedModuleTemplates.resolve(moduleId).path().fromCanonical(canonicalPath);

        wiring.fileSystem.put(path.canonical(), getResourceAsString(testResourcePath));
        wiring.pathResolver.add(moduleId, path);

        return path;
    }

    protected void assertLosslessRoundtrip(@NotNull IntegrationTestWiring wiring) {
        var beforeReadSnapshot = wiring.fileSystem.getSnapshot();

        wiring.ensureLoadedService.ensureLoaded(moduleId);

        var moduleSnapshot = wiring.store.getSnapshot().getModule(moduleId);

        Assert.assertNotNull("Module snapshot should not be null", moduleSnapshot);

        Assert.assertEquals("Module snapshot should define locale values for de & en",
            Set.of(new LocaleId("de"), new LocaleId("en")), moduleSnapshot.locales());

        Assert.assertFalse("Module snapshot should contain actual translations",
            moduleSnapshot.translations().isEmpty());

        wiring.fileSystem.clear();

        wiring.ensurePersistService.ensurePersist(moduleId);

        var afterWriteSnapshot = wiring.fileSystem.getSnapshot();

        Assert.assertEquals("File system snapshot should equal before snapshot after write",
            beforeReadSnapshot, afterWriteSnapshot);
    }
}
