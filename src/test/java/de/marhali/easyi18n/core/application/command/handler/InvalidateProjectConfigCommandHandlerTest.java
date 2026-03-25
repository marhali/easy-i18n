package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.InvalidateProjectConfigCommand;
import de.marhali.easyi18n.core.application.service.CachedModuleRules;
import de.marhali.easyi18n.core.application.service.CachedModuleTemplates;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ProjectConfigChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link InvalidateProjectConfigCommandHandler}.
 *
 * @author marhali
 */
public class InvalidateProjectConfigCommandHandlerTest {

    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        InvalidateProjectConfigCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var projectConfigPort = new InMemoryProjectConfigAdapter();
        var store = new InMemoryI18nStore(new SortableImplementationProvider(projectConfigPort));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new InvalidateProjectConfigCommandHandler(
            store,
            new CachedModuleTemplates(projectConfigPort),
            new CachedModuleRules(projectConfigPort),
            eventPublisher
        );
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateModule(Fixture fixture, ModuleId moduleId) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(moduleId);
            module.addLocale(EN);
            module.getOrCreateTranslation(I18nKey.of("key")).put(EN, I18nValue.fromQuotedPrimitive("value"));
        });
    }

    @Test
    public void test_store_is_cleared() {
        var fixture = buildFixture();
        populateModule(fixture, new ModuleId("moduleA"));
        populateModule(fixture, new ModuleId("moduleB"));

        fixture.handler().handle(new InvalidateProjectConfigCommand());

        Assert.assertTrue("Expected store to be empty after invalidation",
            fixture.store().getSnapshot().modules().isEmpty());
    }

    @Test
    public void test_project_config_changed_event_is_published() {
        var fixture = buildFixture();

        fixture.handler().handle(new InvalidateProjectConfigCommand());

        Assert.assertTrue("Last published event is ProjectConfigChanged",
            fixture.eventPublisher().getLastEvent() instanceof ProjectConfigChanged);
    }
}
