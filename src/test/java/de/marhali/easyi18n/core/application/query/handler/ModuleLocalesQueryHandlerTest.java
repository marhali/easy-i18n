package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.ModuleLocalesQuery;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Unit tests for {@link ModuleLocalesQueryHandler}.
 *
 * @author marhali
 */
public class ModuleLocalesQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");

    private record Fixture(ModuleLocalesQueryHandler handler, InMemoryI18nStore store) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var handler = new ModuleLocalesQueryHandler(new DummyEnsureLoadedService(), store);
        return new Fixture(handler, store);
    }

    private void populateLocales(Fixture fixture, LocaleId... locales) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            for (LocaleId locale : locales) {
                module.addLocale(locale);
            }
        });
    }

    @Test
    public void test_returns_locales_from_loaded_module() {
        var fixture = buildFixture();
        populateLocales(fixture, new LocaleId("en"), new LocaleId("de"));

        Set<LocaleId> result = fixture.handler().handle(new ModuleLocalesQuery(MODULE_ID));

        Assert.assertEquals(Set.of(new LocaleId("en"), new LocaleId("de")), result);
    }

    @Test
    public void test_empty_module_returns_empty_set() {
        var fixture = buildFixture();
        fixture.store().mutate(project -> project.getOrCreateModule(MODULE_ID));

        Set<LocaleId> result = fixture.handler().handle(new ModuleLocalesQuery(MODULE_ID));

        Assert.assertTrue(result.isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void test_module_not_in_store_throws() {
        var fixture = buildFixture();

        fixture.handler().handle(new ModuleLocalesQuery(new ModuleId("unknown")));
    }
}
