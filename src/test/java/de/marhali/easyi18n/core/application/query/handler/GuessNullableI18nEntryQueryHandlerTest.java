package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.GuessNullableI18nEntryQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleTemplates;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link GuessNullableI18nEntryQueryHandler}.
 *
 * @author marhali
 */
public class GuessNullableI18nEntryQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");
    private static final LocaleId PREVIEW_LOCALE = new LocaleId("en");

    private GuessNullableI18nEntryQueryHandler buildHandler(@NotNull String keyTemplate) {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(MODULE_ID)
            .pathTemplate("translations.json")
            .keyTemplate(keyTemplate)
            .build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder()
                .previewLocale(PREVIEW_LOCALE)
                .modules(List.of(module))
                .build()
        );
        return new GuessNullableI18nEntryQueryHandler(
            new CachedModuleTemplates(projectConfigPort),
            projectConfigPort
        );
    }

    @Test
    public void test_input_with_delimiter_is_guessed_as_key() {
        var handler = buildHandler("{fileKey:.}");

        NullableI18nEntry result = handler.handle(new GuessNullableI18nEntryQuery(MODULE_ID, "greeting.title"));

        Assert.assertNotNull("Expected key to be set", result.key());
        Assert.assertNull("Expected content to be null for key guess", result.content());
        Assert.assertEquals("greeting.title", result.key().canonical());
    }

    @Test
    public void test_plain_input_is_guessed_as_value() {
        var handler = buildHandler("{fileKey:.}");

        NullableI18nEntry result = handler.handle(new GuessNullableI18nEntryQuery(MODULE_ID, "Hello World"));

        Assert.assertNull("Expected key to be null for value guess", result.key());
        Assert.assertNotNull("Expected content to be set", result.content());
        Assert.assertTrue(result.content().hasLocale(PREVIEW_LOCALE));
        Assert.assertEquals("Hello World", result.content().values().get(PREVIEW_LOCALE).getAsPrimitive().getText());
    }
}
