package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.UpdateI18nKeyCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link UpdateI18nKeyCommandHandler}.
 *
 * @author marhali
 */
public class UpdateI18nKeyCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        UpdateI18nKeyCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new UpdateI18nKeyCommandHandler(
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
    public void test_old_key_is_removed() {
        var fixture = buildFixture();
        var oldKey = I18nKey.of("old.greeting");
        var newKey = I18nKey.of("new.greeting");
        populateTranslation(fixture, oldKey, I18nValue.fromEscaped("Hello"));

        fixture.handler().handle(new UpdateI18nKeyCommand(MODULE_ID, oldKey, newKey));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertFalse("Expected old key to be removed", module.hasTranslation(oldKey));
    }

    @Test
    public void test_new_key_contains_preserved_content() {
        var fixture = buildFixture();
        var oldKey = I18nKey.of("old.greeting");
        var newKey = I18nKey.of("new.greeting");
        var value = I18nValue.fromEscaped("Hello");
        populateTranslation(fixture, oldKey, value);

        fixture.handler().handle(new UpdateI18nKeyCommand(MODULE_ID, oldKey, newKey));

        var content = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(newKey);
        Assert.assertEquals("Hello", content.values().get(EN).raw());
    }

    @Test(expected = IllegalStateException.class)
    public void test_missing_old_key_throws() {
        var fixture = buildFixture();

        fixture.handler().handle(new UpdateI18nKeyCommand(MODULE_ID, I18nKey.of("nonExistent"), I18nKey.of("new")));
    }

    @Test
    public void test_module_changed_event_is_published_with_new_key() {
        var fixture = buildFixture();
        var oldKey = I18nKey.of("old");
        var newKey = I18nKey.of("new");
        populateTranslation(fixture, oldKey, I18nValue.fromEscaped("Hello"));

        fixture.handler().handle(new UpdateI18nKeyCommand(MODULE_ID, oldKey, newKey));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertEquals(newKey, event.key());
    }
}
