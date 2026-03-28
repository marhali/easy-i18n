package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.ReloadCommand;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ProjectReloaded;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link ReloadCommandHandler}.
 *
 * @author marhali
 */
public class ReloadCommandHandlerTest {

    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        ReloadCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new ReloadCommandHandler(store, eventPublisher);
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateModule(Fixture fixture, ModuleId moduleId) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(moduleId);
            module.addLocale(EN);
            module.getOrCreateTranslation(I18nKey.of("key")).put(EN, I18nValue.fromEscaped("value"));
        });
    }

    @Test
    public void test_store_is_cleared() {
        var fixture = buildFixture();
        populateModule(fixture, new ModuleId("moduleA"));
        populateModule(fixture, new ModuleId("moduleB"));

        fixture.handler().handle(ReloadCommand.reloadAll());

        Assert.assertTrue("Expected store to be empty after reload",
            fixture.store().getSnapshot().modules().isEmpty());
    }

    @Test
    public void test_project_reloaded_event_is_published() {
        var fixture = buildFixture();

        fixture.handler().handle(ReloadCommand.reloadAll());

        Assert.assertTrue("Last published event is ProjectReloaded",
            fixture.eventPublisher().getLastEvent() instanceof ProjectReloaded);
    }
}
