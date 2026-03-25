package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.UpdatePartialI18nKeyCommand;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.DummyEnsurePersistService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link UpdatePartialI18nKeyCommandHandler}.
 *
 * @author marhali
 */
public class UpdatePartialI18nKeyCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        UpdatePartialI18nKeyCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new UpdatePartialI18nKeyCommandHandler(
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
            module.getOrCreateTranslation(key).put(EN, I18nValue.fromQuotedPrimitive("value"));
        });
    }

    @Test
    public void test_matching_keys_are_renamed() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("ns.old.title"));
        populateTranslation(fixture, I18nKey.of("ns.old.description"));

        fixture.handler().handle(new UpdatePartialI18nKeyCommand(MODULE_ID, List.of("ns."), "old", "new"));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertTrue(module.hasTranslation(I18nKey.of("ns.new.title")));
        Assert.assertTrue(module.hasTranslation(I18nKey.of("ns.new.description")));
        Assert.assertFalse(module.hasTranslation(I18nKey.of("ns.old.title")));
        Assert.assertFalse(module.hasTranslation(I18nKey.of("ns.old.description")));
    }

    @Test
    public void test_non_matching_keys_are_unchanged() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("ns.old.title"));
        populateTranslation(fixture, I18nKey.of("other.key"));

        fixture.handler().handle(new UpdatePartialI18nKeyCommand(MODULE_ID, List.of("ns."), "old", "new"));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertTrue("Non-matching key should remain unchanged", module.hasTranslation(I18nKey.of("other.key")));
    }

    @Test
    public void test_content_is_preserved_after_rename() {
        var fixture = buildFixture();
        var key = I18nKey.of("ns.old.title");
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, I18nValue.fromQuotedPrimitive("My Title"));
        });

        fixture.handler().handle(new UpdatePartialI18nKeyCommand(MODULE_ID, List.of("ns."), "old", "new"));

        var content = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID)
            .getTranslationOrThrow(I18nKey.of("ns.new.title"));
        Assert.assertEquals("My Title", content.values().get(EN).getAsPrimitive().getText());
    }

    @Test
    public void test_module_changed_event_is_published() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("ns.old.title"));

        fixture.handler().handle(new UpdatePartialI18nKeyCommand(MODULE_ID, List.of("ns."), "old", "new"));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertNull(event.key());
    }
}
