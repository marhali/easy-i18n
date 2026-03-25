package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.ConfiguredModulesQuery;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Set;

/**
 * Unit tests for {@link ConfiguredModulesQueryHandler}.
 *
 * @author marhali
 */
public class ConfiguredModulesQueryHandlerTest {

    private ConfiguredModulesQueryHandler handlerWithModules(List<ProjectConfigModule> modules) {
        var config = ProjectConfig.fromDefaultPreset().toBuilder()
            .modules(modules)
            .build();
        return new ConfiguredModulesQueryHandler(new InMemoryProjectConfigAdapter(config));
    }

    @Test
    public void test_no_modules_returns_empty_set() {
        var handler = handlerWithModules(List.of());

        Set<ModuleId> result = handler.handle(new ConfiguredModulesQuery());

        Assert.assertNotNull(result);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void test_single_module_returns_its_id() {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(new ModuleId("myModuleId"))
            .build();

        var handler = handlerWithModules(List.of(module));

        Set<ModuleId> result = handler.handle(new ConfiguredModulesQuery());

        Assert.assertEquals(Set.of(module.id()), result);
    }

    @Test
    public void test_multiple_modules_returns_all_ids() {
        var moduleA = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(new ModuleId("moduleA"))
            .build();
        var moduleB = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(new ModuleId("moduleB"))
            .build();

        var handler = handlerWithModules(List.of(moduleA, moduleB));

        Set<ModuleId> result = handler.handle(new ConfiguredModulesQuery());

        Assert.assertEquals(Set.of(moduleA.id(), moduleB.id()), result);
    }
}
