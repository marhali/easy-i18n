package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.AllModuleI18nEntryPreviewQuery;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link AllModuleI18nEntryPreviewQueryHandler}.
 *
 * @author marhali
 */
public class AllModuleI18nEntryPreviewQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");

    private record Fixture(
        AllModuleI18nEntryPreviewQueryHandler handler,
        InMemoryI18nStore store,
        InMemoryProjectConfigAdapter projectConfigPort
    ) {}

    private Fixture buildFixture() {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder().id(MODULE_ID).build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder()
                .previewLocale(EN)
                .modules(List.of(module))
                .build()
        );
        var store = new InMemoryI18nStore(new SortableImplementationProvider(projectConfigPort));
        var handler = new AllModuleI18nEntryPreviewQueryHandler(store, projectConfigPort);
        return new Fixture(handler, store, projectConfigPort);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            MutableI18nContent translation = module.getOrCreateTranslation(key);
            translation.put(EN, value);
            translation.put(DE, I18nValue.fromQuotedPrimitive("Any not relevant value for non preview locale"));
        });
    }

    @Test
    public void test_module_not_loaded_returns_unavailable() {
        var fixture = buildFixture();

        PossiblyUnavailable<List<I18nEntryPreview>> response = fixture.handler().handle(
            new AllModuleI18nEntryPreviewQuery(MODULE_ID)
        );

        Assert.assertFalse(response.available());
    }

    @Test
    public void test_returns_all_entry_previews() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("greeting"), I18nValue.fromQuotedPrimitive("Hello"));
        populateTranslation(fixture, I18nKey.of("farewell"), I18nValue.fromQuotedPrimitive("Bye"));

        PossiblyUnavailable<List<I18nEntryPreview>> response = fixture.handler().handle(
            new AllModuleI18nEntryPreviewQuery(MODULE_ID)
        );

        Assert.assertTrue(response.available());
        Assert.assertNotNull(response.result());
        Assert.assertEquals(2, response.result().size());
    }

    @Test
    public void test_preview_value_uses_configured_preview_locale() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("greeting"), I18nValue.fromQuotedPrimitive("Hello"));

        PossiblyUnavailable<List<I18nEntryPreview>> response = fixture.handler().handle(
            new AllModuleI18nEntryPreviewQuery(MODULE_ID)
        );

        Assert.assertTrue(response.available());
        Assert.assertNotNull(response.result());
        Assert.assertEquals(1, response.result().size());
        var preview = response.result().getFirst();
        Assert.assertNotNull(preview.previewValue());
        Assert.assertEquals("Hello", preview.previewValue().getAsPrimitive().getText());
    }
}
