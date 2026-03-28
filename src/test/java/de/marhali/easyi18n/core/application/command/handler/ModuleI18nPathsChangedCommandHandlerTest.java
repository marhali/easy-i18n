package de.marhali.easyi18n.core.application.command.handler;

import de.marhali.easyi18n.core.adapters.InMemoryDomainEventPublisherAdapter;
import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.command.ModuleI18nPathsChangedCommand;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.event.ModuleChanged;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Unit tests for {@link ModuleI18nPathsChangedCommandHandler}.
 *
 * @author marhali
 */
public class ModuleI18nPathsChangedCommandHandlerTest {

    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(
        ModuleI18nPathsChangedCommandHandler handler,
        InMemoryI18nStore store,
        InMemoryDomainEventPublisherAdapter eventPublisher
    ) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var eventPublisher = new InMemoryDomainEventPublisherAdapter();
        var handler = new ModuleI18nPathsChangedCommandHandler(store, eventPublisher);
        return new Fixture(handler, store, eventPublisher);
    }

    private void populateModule(Fixture fixture, ModuleId moduleId) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(moduleId);
            module.addLocale(EN);
            module.getOrCreateTranslation(I18nKey.of("key")).put(EN, I18nValue.fromEscaped("value"));
        });
    }

    private ModuleI18nPath anyPathFor(ModuleId moduleId) {
        return new ModuleI18nPath(moduleId, new I18nPath("any/path.json", new I18nParams(Map.of())));
    }

    @Test
    public void test_affected_modules_are_cleared_in_store() {
        var fixture = buildFixture();
        var moduleId = new ModuleId("myModule");
        populateModule(fixture, moduleId);

        fixture.handler().handle(new ModuleI18nPathsChangedCommand(Set.of(anyPathFor(moduleId))));

        Assert.assertFalse("Expected module to be cleared from store",
            fixture.store().getSnapshot().hasModule(moduleId));
    }

    @Test
    public void test_unaffected_modules_remain_in_store() {
        var fixture = buildFixture();
        var affected = new ModuleId("affected");
        var unaffected = new ModuleId("unaffected");
        populateModule(fixture, affected);
        populateModule(fixture, unaffected);

        fixture.handler().handle(new ModuleI18nPathsChangedCommand(Set.of(anyPathFor(affected))));

        Assert.assertTrue("Expected unaffected module to remain in store",
            fixture.store().getSnapshot().hasModule(unaffected));
    }

    @Test
    public void test_module_changed_event_per_affected_module_is_published() {
        var fixture = buildFixture();
        var moduleA = new ModuleId("moduleA");
        var moduleB = new ModuleId("moduleB");
        populateModule(fixture, moduleA);
        populateModule(fixture, moduleB);

        fixture.handler().handle(new ModuleI18nPathsChangedCommand(
            Set.of(anyPathFor(moduleA), anyPathFor(moduleB))
        ));

        var publishedModuleIds = fixture.eventPublisher().getEvents().stream()
            .map(e -> ((ModuleChanged) e).moduleId())
            .collect(Collectors.toSet());

        Assert.assertEquals(Set.of(moduleA, moduleB), publishedModuleIds);
    }
}
