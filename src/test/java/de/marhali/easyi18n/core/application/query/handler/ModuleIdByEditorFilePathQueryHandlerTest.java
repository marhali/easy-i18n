package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.ModuleIdByEditorFilePathQuery;
import de.marhali.easyi18n.core.application.service.ModuleIdByEditorFilePathResolver;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorFilePath;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link ModuleIdByEditorFilePathQueryHandler}.
 *
 * @author marhali
 */
public class ModuleIdByEditorFilePathQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final String rootDirectory = "src/main/java";

    private ModuleIdByEditorFilePathQueryHandler buildHandler(@NotNull String rootDirectory) {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(MODULE_ID)
            .rootDirectory(rootDirectory)
            .build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().modules(List.of(module)).build()
        );
        return new ModuleIdByEditorFilePathQueryHandler(new ModuleIdByEditorFilePathResolver(projectConfigPort));
    }

    @Test
    public void test_matching_path_returns_module_id() {
        var handler = buildHandler(rootDirectory);

        Optional<ModuleId> result = handler.handle(
            new ModuleIdByEditorFilePathQuery(new EditorFilePath("src/main/java/Main.java"))
        );

        Assert.assertEquals(Optional.of(MODULE_ID), result);
    }

    @Test
    public void test_non_matching_path_returns_empty() {
        var handler = buildHandler(rootDirectory);

        Optional<ModuleId> result = handler.handle(
            new ModuleIdByEditorFilePathQuery(new EditorFilePath("other/path/en.json"))
        );

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void test_null_path_returns_empty() {
        var handler = buildHandler(rootDirectory);

        Optional<ModuleId> result = handler.handle(
            new ModuleIdByEditorFilePathQuery(new EditorFilePath(null))
        );

        Assert.assertTrue(result.isEmpty());
    }
}
