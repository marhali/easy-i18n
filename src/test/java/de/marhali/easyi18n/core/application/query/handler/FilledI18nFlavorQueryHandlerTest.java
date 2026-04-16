package de.marhali.easyi18n.core.application.query.handler;

import de.marhali.easyi18n.core.adapters.InMemoryProjectConfigAdapter;
import de.marhali.easyi18n.core.application.query.FilledI18nFlavorQuery;
import de.marhali.easyi18n.core.application.service.CachedModuleTemplates;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nKey;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Unit tests for {@link FilledI18nFlavorQueryHandler}.
 *
 * @author marhali
 */
public class FilledI18nFlavorQueryHandlerTest {

    private static final ModuleId MODULE_ID = new ModuleId("testModule");

    private FilledI18nFlavorQueryHandler buildHandler(@NotNull String editorFlavorTemplate) {
        var module = ProjectConfigModule.fromDefaultPreset().toBuilder()
            .id(MODULE_ID)
            .editorFlavorTemplate(editorFlavorTemplate)
            .fileTemplate("[{fileKey}]")
            .pathTemplate("locales.json")
            .build();
        var projectConfigPort = new InMemoryProjectConfigAdapter(
            ProjectConfig.fromDefaultPreset().toBuilder().modules(List.of(module)).build()
        );
        return new FilledI18nFlavorQueryHandler(new CachedModuleTemplates(projectConfigPort));
    }

    @Test
    public void test_key_is_filled_into_flavor_template() {
        var handler = buildHandler("$i18n.t(\"{i18nKey}\")");

        String result = handler.handle(new FilledI18nFlavorQuery(MODULE_ID, I18nKey.of("greeting")));

        Assert.assertEquals("Expected result to contain the key value",
            "$i18n.t(\"greeting\")", result);
    }
}
