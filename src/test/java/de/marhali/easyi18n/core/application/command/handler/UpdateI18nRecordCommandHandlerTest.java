package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.UpdateI18nRecordCommand;
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
 * Unit tests for {@link UpdateI18nRecordCommandHandler}.
 *
 * @author marhali
 */
public class UpdateI18nRecordCommandHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");

    private record Fixture(
        UpdateI18nRecordCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var projectConfigPort = new InMemoryProjectConfigAdapter();
        var implementationProvider = new SortableImplementationProvider(projectConfigPort);
        var store = new InMemoryI18nStore(implementationProvider);
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new UpdateI18nRecordCommandHandler(
            implementationProvider,
            new DummyEnsureLoadedService(),
            new DummyEnsurePersistService(),
            store,
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, I18nValue enValue) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, enValue);
        });
    }

    @Test
    public void test_record_content_is_replaced() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, I18nValue.fromQuotedPrimitive("Hello"));

        var updatedContent = new I18nContent(Map.of(EN, I18nValue.fromQuotedPrimitive("Hi")), null);
        fixture.handler().handle(new UpdateI18nRecordCommand(MODULE_ID, key, key, updatedContent));

        var content = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID).getTranslationOrThrow(key);
        Assert.assertEquals("Hi", content.values().get(EN).getAsPrimitive().getText());
    }

    @Test
    public void test_key_rename_removes_old_key() {
        var fixture = buildFixture();
        var originKey = I18nKey.of("old.greeting");
        var newKey = I18nKey.of("new.greeting");
        populateTranslation(fixture, originKey, I18nValue.fromQuotedPrimitive("Hello"));

        var content = new I18nContent(Map.of(EN, I18nValue.fromQuotedPrimitive("Hello")), null);
        fixture.handler().handle(new UpdateI18nRecordCommand(MODULE_ID, originKey, newKey, content));

        var module = fixture.store().getSnapshot().getModuleOrThrow(MODULE_ID);
        Assert.assertFalse("Expected old key to be removed", module.hasTranslation(originKey));
        Assert.assertTrue("Expected new key to be present", module.hasTranslation(newKey));
    }

    @Test
    public void test_module_changed_event_is_published_with_new_key() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, I18nValue.fromQuotedPrimitive("Hello"));

        var newKey = I18nKey.of("farewell");
        var content = new I18nContent(Map.of(EN, I18nValue.fromQuotedPrimitive("Bye")), null);
        fixture.handler().handle(new UpdateI18nRecordCommand(MODULE_ID, key, newKey, content));

        var event = (ModuleChanged) fixture.eventPublisher().getLastEvent();
        Assert.assertEquals(MODULE_ID, event.moduleId());
        Assert.assertEquals(newKey, event.key());
    }
}
