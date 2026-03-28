package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author marhali
 */
public class LocalesOrderServiceTest {

    private static final LocaleId EN = new LocaleId("en");
    private static final LocaleId DE = new LocaleId("de");
    private static final LocaleId FR = new LocaleId("fr");

    private LocalesOrderService buildService(LocaleId previewLocale) {
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(previewLocale).build()
        );
        return new LocalesOrderService(projectConfigPort);
    }

    @Test
    public void test_preview_locale_is_moved_to_first_position() {
        var service = buildService(DE);

        var input = new LinkedHashSet<>(Set.of(EN, DE, FR));
        var result = service.orderByPreviewLocale(input);

        Assert.assertEquals(DE, result.getFirst());
    }

    @Test
    public void test_all_locales_are_retained_when_preview_locale_present() {
        var service = buildService(DE);

        var input = new LinkedHashSet<>(Set.of(EN, DE, FR));
        var result = service.orderByPreviewLocale(input);

        Assert.assertEquals(Set.of(EN, DE, FR), result);
    }

    @Test
    public void test_order_unchanged_when_preview_locale_not_in_set() {
        var service = buildService(FR);

        var input = new LinkedHashSet<>(Set.of(EN, DE));
        var result = service.orderByPreviewLocale(input);

        Assert.assertEquals(new LinkedHashSet<>(Set.of(EN, DE)), result);
    }

    @Test
    public void test_single_locale_matching_preview_locale() {
        var service = buildService(EN);

        var result = service.orderByPreviewLocale(new LinkedHashSet<>(Set.of(EN)));

        Assert.assertEquals(EN, result.getFirst());
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void test_empty_set_returns_empty_set() {
        var service = buildService(EN);

        var result = service.orderByPreviewLocale(new LinkedHashSet<>());

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void test_config_update_is_reflected() {
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(EN).build()
        );
        var service = new LocalesOrderService(projectConfigPort);

        projectConfigPort.updateProjectConfig(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(DE).build()
        );

        var input = new LinkedHashSet<>(Set.of(EN, DE, FR));
        var result = service.orderByPreviewLocale(input);

        Assert.assertEquals(DE, result.getFirst());
    }
}
