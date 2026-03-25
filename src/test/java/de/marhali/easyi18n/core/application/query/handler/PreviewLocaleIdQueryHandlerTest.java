package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.PreviewLocaleIdQuery;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link PreviewLocaleIdQueryHandler}.
 *
 * @author marhali
 */
public class PreviewLocaleIdQueryHandlerTest {

    @Test
    public void test_returns_configured_preview_locale() {
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(new LocaleId("de")).build()
        );
        var handler = new PreviewLocaleIdQueryHandler(projectConfigPort);

        LocaleId result = handler.handle(new PreviewLocaleIdQuery());

        Assert.assertEquals(new LocaleId("de"), result);
    }

    @Test
    public void test_config_update_is_reflected() {
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(new LocaleId("en")).build()
        );
        var handler = new PreviewLocaleIdQueryHandler(projectConfigPort);

        projectConfigPort.updateProjectConfig(
            ProjectConfig.fromDefaultPreset().toBuilder().previewLocale(new LocaleId("fr")).build()
        );

        Assert.assertEquals(new LocaleId("fr"), handler.handle(new PreviewLocaleIdQuery()));
    }
}
