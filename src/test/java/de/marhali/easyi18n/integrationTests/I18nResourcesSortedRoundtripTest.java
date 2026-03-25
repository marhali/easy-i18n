package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.*;
import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author marhali
 */
public class I18nResourcesSortedRoundtripTest extends I18nResourcesRoundtripTestBase {

    protected static final @NotNull ProjectConfigBuilder projectConfigBuilder = ProjectConfig.builder()
        .keyComment(false)
        .sorting(true)
        .previewLocale(new LocaleId("en"))
        .modules();

    @Test
    public void test_sorted_roundtrip_json() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/translations.json")
                    .fileCodec(FileCodec.JSON)
                    .fileTemplate("[{locale}][{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        var path = provideTranslationResource(wiring, "locales/translations.json",
            "roundtrip_sorted/json/before.json");

        assertSortedRoundtrip(wiring, Map.of(path, "roundtrip_sorted/json/after.json"));
    }

    @Test
    public void test_sorted_roundtrip_json5() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/translations.json5")
                    .fileCodec(FileCodec.JSON5)
                    .fileTemplate("[{locale}][{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        var path = provideTranslationResource(wiring, "locales/translations.json5",
            "roundtrip_sorted/json5/before.json5");

        assertSortedRoundtrip(wiring, Map.of(path, "roundtrip_sorted/json5/after.json5"));
    }

    @Test
    public void test_sorted_roundtrip_yaml() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/translations.yaml")
                    .fileCodec(FileCodec.YAML)
                    .fileTemplate("[{locale}][{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        var path = provideTranslationResource(wiring, "locales/translations.yaml",
            "roundtrip_sorted/yaml/before.yaml");

        assertSortedRoundtrip(wiring, Map.of(path, "roundtrip_sorted/yaml/after.yaml"));
    }

    @Test
    public void test_sorted_roundtrip_properties() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/translations.properties")
                    .fileCodec(FileCodec.PROPERTIES)
                    .fileTemplate("[{locale}|{fileKey:.}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        var path = provideTranslationResource(wiring, "locales/translations.properties",
            "roundtrip_sorted/properties/before.properties");

        assertSortedRoundtrip(wiring, Map.of(path, "roundtrip_sorted/properties/after.properties"));
    }
}
