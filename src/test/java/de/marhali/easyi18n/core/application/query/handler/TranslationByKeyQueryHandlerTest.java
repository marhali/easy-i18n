package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.TranslationByKeyQuery;
import de.marhali.easyi18n.core.application.service.DummyEnsureLoadedService;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
 * Unit tests for {@link TranslationByKeyQueryHandler}.
 *
 * @author marhali
 */
public class TranslationByKeyQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");

    private record Fixture(TranslationByKeyQueryHandler handler, InMemoryI18nStore store) {}

    private Fixture buildFixture() {
        var store = new InMemoryI18nStore(new SortableImplementationProvider(new InMemoryProjectConfigAdapter()));
        var handler = new TranslationByKeyQueryHandler(new DummyEnsureLoadedService(), store);
        return new Fixture(handler, store);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, LocaleId localeId, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(localeId);
            module.getOrCreateTranslation(key).put(localeId, value);
        });
    }

    @Test
    public void test_existing_key_returns_content() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, EN, I18nValue.fromEscaped("Hello"));

        Optional<I18nContent> result = fixture.handler().handle(new TranslationByKeyQuery(MODULE_ID, key));

        Assert.assertTrue("Expected content to be present for existing key", result.isPresent());
    }

    @Test
    public void test_existing_key_returns_correct_locale_values() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, EN, I18nValue.fromEscaped("Hello"));
        populateTranslation(fixture, key, DE, I18nValue.fromEscaped("Hallo"));

        Optional<I18nContent> result = fixture.handler().handle(new TranslationByKeyQuery(MODULE_ID, key));

        Assert.assertTrue(result.isPresent());
        Assert.assertEquals("Hello", result.get().values().get(EN).raw());
        Assert.assertEquals("Hallo", result.get().values().get(DE).raw());
    }

    @Test
    public void test_missing_key_returns_empty_optional() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("other.key"), EN, I18nValue.fromEscaped("Hello"));

        Optional<I18nContent> result = fixture.handler().handle(
            new TranslationByKeyQuery(MODULE_ID, I18nKey.of("does.not.exist"))
        );

        Assert.assertFalse("Expected empty Optional for non-existent key", result.isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void test_module_not_in_store_throws() {
        var fixture = buildFixture();

        fixture.handler().handle(new TranslationByKeyQuery(new ModuleId("unknownModule"), I18nKey.of("any.key")));
    }
}
