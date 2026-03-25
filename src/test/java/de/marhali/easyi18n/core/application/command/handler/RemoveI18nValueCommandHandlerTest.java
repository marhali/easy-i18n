package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.RemoveI18nValueCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link RemoveI18nValueCommandHandler}.
 *
 * @author marhali
 */
public class RemoveI18nValueCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");

    private record Fixture(
        RemoveI18nValueCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new RemoveI18nValueCommandHandler(
            new DummyEnsureLoadedService(),
            new DummyEnsurePersistService(),
            store,
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, LocaleId localeId, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(localeId);
            module.getOrCreateTranslation(key).put(localeId, value);
        });
    }

    @Test
    public void test_locale_value_is_removed() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, EN, I18nValue.fromQuotedPrimitive("Hello"));
        populateTranslation(fixture, key, DE, I18nValue.fromQuotedPrimitive("Hallo"));

        fixture.handler().handle(new RemoveI18nValueCommand(MODULE_ID, key, EN));

        var content = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(key);
        Assert.assertFalse("Expected 'en' value to be removed", content.hasLocale(EN));
        Assert.assertTrue("Expected 'de' value to remain", content.hasLocale(DE));
    }

    @Test(expected = IllegalStateException.class)
    public void test_missing_translation_throws() {
        var fixture = buildFixture();

        fixture.handler().handle(new RemoveI18nValueCommand(MODULE_ID, I18nKey.of("nonExistent"), EN));
    }

    @Test
    public void test_module_changed_event_is_published() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, EN, I18nValue.fromQuotedPrimitive("Hello"));

        fixture.handler().handle(new RemoveI18nValueCommand(MODULE_ID, key, EN));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertEquals(key, event.key());
    }
}
