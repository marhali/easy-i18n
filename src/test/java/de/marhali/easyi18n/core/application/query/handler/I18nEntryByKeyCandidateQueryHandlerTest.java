package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.cqrs.PossiblyUnavailable;
import de.marhali.easyi18n.core.application.query.I18nEntryByKeyCandidateQuery;
import de.marhali.easyi18n.core.application.service.I18nKeyCandidateResolver;
import de.marhali.easyi18n.core.application.service.SortableImplementationProvider;
import de.marhali.easyi18n.core.application.state.InMemoryI18nStore;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

/**
 * Unit tests for {@link I18nEntryByKeyCandidateQueryHandler}.
 *
 * @author marhali
 */
public class I18nEntryByKeyCandidateQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId EN = new LocaleId("en");

    private record Fixture(I18nEntryByKeyCandidateQueryHandler handler, InMemoryI18nStore store) {}

    private Fixture buildFixture() {
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder()
                .module(ProjectConfigModule.fromDefaultPreset().toBuilder()
                    .id(MODULE_ID)
                    .defaultKeyPrefixes(Set.of(I18nKeyPrefix.of("common:")))
                    .build())
                .build()
        );
        var store = new InMemoryI18nStore(new SortableImplementationProvider(projectConfigPort));
        var resolver = new I18nKeyCandidateResolver(projectConfigPort, store);
        var handler = new I18nEntryByKeyCandidateQueryHandler(store, resolver);
        return new Fixture(handler, store);
    }

    private void populateTranslation(Fixture fixture, I18nKey key, I18nValue value) {
        fixture.store().mutate(project -> {
            var module = project.getOrCreateModule(MODULE_ID);
            module.addLocale(EN);
            module.getOrCreateTranslation(key).put(EN, value);
        });
    }

    @Test
    public void test_module_not_loaded_returns_unavailable() {
        var fixture = buildFixture();

        PossiblyUnavailable<Optional<I18nEntry>> response = fixture.handler().handle(
            new I18nEntryByKeyCandidateQuery(MODULE_ID, I18nKeyCandidate.of("greeting"))
        );

        Assert.assertFalse(response.available());
    }

    @Test
    public void test_existing_key_returns_available_entry() {
        var fixture = buildFixture();
        var key = I18nKey.of("greeting");
        populateTranslation(fixture, key, I18nValue.fromQuotedPrimitive("Hello"));

        PossiblyUnavailable<Optional<I18nEntry>> response = fixture.handler().handle(
            new I18nEntryByKeyCandidateQuery(MODULE_ID, I18nKeyCandidate.of("greeting"))
        );

        Assert.assertTrue(response.available());
        Assert.assertNotNull(response.result());
        Assert.assertTrue(response.result().isPresent());
        Assert.assertEquals(key, response.result().get().key());
    }

    @Test
    public void test_missing_key_returns_available_empty_optional() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("other"), I18nValue.fromQuotedPrimitive("Hello"));

        PossiblyUnavailable<Optional<I18nEntry>> response = fixture.handler().handle(
            new I18nEntryByKeyCandidateQuery(MODULE_ID, I18nKeyCandidate.of("nonExistent"))
        );

        Assert.assertTrue(response.available());
        Assert.assertNotNull(response.result());
        Assert.assertFalse(response.result().isPresent());
    }

    @Test
    public void test_prefixed_keys_returns_available_entry() {
        var fixture = buildFixture();
        populateTranslation(fixture, I18nKey.of("common:anyCommonKey"), I18nValue.fromQuotedPrimitive("Hello"));

        PossiblyUnavailable<Optional<I18nEntry>> response = fixture.handler().handle(
            new I18nEntryByKeyCandidateQuery(MODULE_ID, I18nKeyCandidate.of("anyCommonKey"))
        );

        Assert.assertTrue(response.available());
        Assert.assertNotNull(response.result());
        Assert.assertTrue(response.result().isPresent());
        Assert.assertEquals(I18nKey.of("common:anyCommonKey"), response.result().get().key());
    }
}
