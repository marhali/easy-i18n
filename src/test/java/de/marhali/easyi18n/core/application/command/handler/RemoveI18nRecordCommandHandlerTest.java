package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.RemoveI18nRecordCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link RemoveI18nRecordCommandHandler}.
 *
 * @author marhali
 */
public class RemoveI18nRecordCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        RemoveI18nRecordCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new RemoveI18nRecordCommandHandler(
            new DummyEnsureLoadedService(),
            new DummyEnsurePersistService(),
            store,
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateTranslation(Fixture fixture, I18nKey key) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, I18nValue.fromEscaped("value"));
        });
    }

    @Test
    public void test_translation_is_removed_from_store() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key);

        fixture.handler().handle(new RemoveI18nRecordCommand(MODULE_ID, key));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertFalse("Expected translation to be removed from store", module.hasTranslation(key));
    }

    @Test
    public void test_other_translations_are_not_affected() {
        var fixture = buildFixture();
        var removed = I18nKey.of("removed");
        var kept = I18nKey.of("kept");
        populateTranslation(fixture, removed);
        populateTranslation(fixture, kept);

        fixture.handler().handle(new RemoveI18nRecordCommand(MODULE_ID, removed));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertTrue("Expected other translation to remain in store", module.hasTranslation(kept));
    }

    @Test
    public void test_module_changed_event_is_published() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key);

        fixture.handler().handle(new RemoveI18nRecordCommand(MODULE_ID, key));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertNull(event.key());
    }
}
