package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.UpdateI18nValueCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link UpdateI18nValueCommandHandler}.
 *
 * @author marhali
 */
public class UpdateI18nValueCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        UpdateI18nValueCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new UpdateI18nValueCommandHandler(
            new DummyEnsureLoadedService(),
            new DummyEnsurePersistService(),
            store,
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, value);
        });
    }

    @Test
    public void test_locale_value_is_updated() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, I18nValue.fromQuotedPrimitive("Hello"));

        fixture.handler().handle(new UpdateI18nValueCommand(MODULE_ID, key, EN, I18nValue.fromQuotedPrimitive("Hi")));

        var content = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(key);
        Assert.assertEquals("Hi", content.values().get(EN).getAsPrimitive().getText());
    }

    @Test(expected = IllegalStateException.class)
    public void test_missing_translation_throws() {
        var fixture = buildFixture();

        fixture.handler().handle(new UpdateI18nValueCommand(MODULE_ID, I18nKey.of("nonExistent"), EN, I18nValue.fromQuotedPrimitive("Hi")));
    }

    @Test
    public void test_module_changed_event_is_published() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, I18nValue.fromQuotedPrimitive("Hello"));

        fixture.handler().handle(new UpdateI18nValueCommand(MODULE_ID, key, EN, I18nValue.fromQuotedPrimitive("Hi")));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertEquals(key, event.key());
    }
}
