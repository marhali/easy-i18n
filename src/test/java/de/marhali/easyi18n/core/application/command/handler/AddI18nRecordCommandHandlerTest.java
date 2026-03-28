package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.AddI18nRecordCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Unit tests for {@link AddI18nRecordCommandHandler}.
 *
 * @author marhali
 */
public class AddI18nRecordCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");

    private record Fixture(
        AddI18nRecordCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new AddI18nRecordCommandHandler(
            new DummyEnsureLoadedService(),
            new DummyEnsurePersistService(),
            store,
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    @Test
    public void test_translation_is_added_to_store() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        var content = new I18nContent(Map.of(EN, I18nValue.fromEscaped("Hello")), null);

        fixture.handler().handle(new AddI18nRecordCommand(MODULE_ID, key, content));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertTrue("Expected translation to be present in store", module.hasTranslation(key));
    }

    @Test
    public void test_locale_values_are_stored_correctly() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        var content = new I18nContent(Map.of(
            EN, I18nValue.fromEscaped("Hello"),
            DE, I18nValue.fromEscaped("Hallo")
        ), null);

        fixture.handler().handle(new AddI18nRecordCommand(MODULE_ID, key, content));

        var stored = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(key);
        Assert.assertEquals("Hello", stored.values().get(EN).raw());
        Assert.assertEquals("Hallo", stored.values().get(DE).raw());
    }

    @Test
    public void test_existing_translation_is_updated() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");

        fixture.handler().handle(new AddI18nRecordCommand(MODULE_ID, key,
            new I18nContent(Map.of(EN, I18nValue.fromEscaped("Hello")), null)));
        fixture.handler().handle(new AddI18nRecordCommand(MODULE_ID, key,
            new I18nContent(Map.of(EN, I18nValue.fromEscaped("Hi")), null)));

        var stored = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(key);
        Assert.assertEquals("Hi", stored.values().get(EN).raw());
    }

    @Test
    public void test_module_changed_event_is_published() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        var content = new I18nContent(Map.of(EN, I18nValue.fromEscaped("Hello")), null);

        fixture.handler().handle(new AddI18nRecordCommand(MODULE_ID, key, content));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertEquals(key, event.key());
    }
}
