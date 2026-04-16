package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.ModuleViewQuery;
import de.marhali.easyi18n.core.application.query.view.ModuleView;
import de.marhali.easyi18n.core.application.query.view.ModuleViewOptions;
import de.marhali.easyi18n.core.application.query.view.ModuleViewType;
import de.marhali.easyi18n.core.application.service.*;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link ModuleViewQueryHandler}.
 *
 * @author marhali
 */
public class ModuleViewQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final ModuleViewOptions TABLE_OPTIONS = new ModuleViewOptions(ModuleViewType.TABLE, null, false, false, false);

    private record Fixture(ModuleViewQueryHandler handler, InMemoryI18nStore store) {}

    private Fixture buildFixture() {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(MODULE_ID)
            .pathTemplate("locales.json")
            .fileTemplate("[{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().modules(List.of(module)).build()
        );
        var store = new InMemoryI18nStore(new SortableImplementationProvider(projectConfigPort));
        var localesOrderService = new LocalesOrderService(projectConfigPort);
        var projector = new ModuleViewProjector(localesOrderService, new CachedModuleTemplates(projectConfigPort));
        var handler = new ModuleViewQueryHandler(new DummyEnsureLoadedService(), store, projector);
        return new Fixture(handler, store);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, value);
        });
    }

    @Test
    public void test_returns_module_view_with_correct_module_id() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("greeting"), I18nValue.fromEscaped("Hello"));

        ModuleView result = fixture.handler().handle(new ModuleViewQuery(MODULE_ID, TABLE_OPTIONS));

        Assert.assertEquals(MODULE_ID, result.moduleId());
    }

    @Test
    public void test_table_view_contains_locales() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("greeting"), I18nValue.fromEscaped("Hello"));

        ModuleView result = fixture.handler().handle(new ModuleViewQuery(MODULE_ID, TABLE_OPTIONS));

        Assert.assertTrue(result.locales().contains(EN));
    }

    @Test
    public void test_table_view_contains_all_translations() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("greeting"), I18nValue.fromEscaped("Hello"));
        populateTranslation(fixture, I18nKey.of("farewell"), I18nValue.fromEscaped("Bye"));

        ModuleView.Table result = (ModuleView.Table) fixture.handler().handle(new ModuleViewQuery(MODULE_ID, TABLE_OPTIONS));

        Assert.assertEquals(2, result.rows().size());
    }
}
