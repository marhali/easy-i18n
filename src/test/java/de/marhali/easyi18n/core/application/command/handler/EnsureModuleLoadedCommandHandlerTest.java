package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.EnsureModuleLoadedCommand;
import de.marhali.easyi18n.core.application.service.*;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link EnsureModuleLoadedCommandHandler}.
 *
 * @author marhali
 */
public class EnsureModuleLoadedCommandHandlerTest {

    private record Fixture(
        EnsureModuleLoadedCommandHandler handler,
        InMemoryI18nStore store
    ) {}

    private Fixture buildFixture(ModuleId moduleId) {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder().id(moduleId).build();
        var projectConfig = ProjectConfig.fromDefaultPreset().toBuilder().modules(List.of(module)).build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(projectConfig);
        var store = new InMemoryI18nStore(new SortableImplementationProvider(projectConfigPort));
        var ensureLoadedService = new DefaultEnsureLoadedService(store, projectConfigPort, new DummyModuleLoader());
        var handler = new EnsureModuleLoadedCommandHandler(ensureLoadedService);
        return new Fixture(handler, store);
    }

    @Test
    public void test_module_is_loaded_into_store() {
        var moduleId = new ModuleId("myModule");
        var fixture = buildFixture(moduleId);

        fixture.handler().handle(new EnsureModuleLoadedCommand(moduleId));

        Assert.assertTrue("Expected module to be present in store after command",
            fixture.store().getSnapshot().hasModule(moduleId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_unconfigured_module_throws() {
        var fixture = buildFixture(new ModuleId("configured"));

        fixture.handler().handle(new EnsureModuleLoadedCommand(new ModuleId("unknown")));
    }
}
