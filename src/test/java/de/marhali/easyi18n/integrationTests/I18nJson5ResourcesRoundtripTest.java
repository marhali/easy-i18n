package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author marhali
 */
public class I18nJson5ResourcesRoundtripTest extends I18nResourcesRoundtripTestBase {

    @Test
    public void test_roundtrip_locale_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}/{pathNamespace}.json5")
                    .fileCodec(FileCodec.JSON5)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de/nsA.json5",
            "roundtrip/json5/locale_dir/de/nsA.json5");
        provideTranslationResource(wiring, "locales/de/nsB.json5",
            "roundtrip/json5/locale_dir/de/nsB.json5");

        provideTranslationResource(wiring, "locales/en/nsA.json5",
            "roundtrip/json5/locale_dir/en/nsA.json5");
        provideTranslationResource(wiring, "locales/en/nsB.json5",
            "roundtrip/json5/locale_dir/en/nsB.json5");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_namespace_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{pathNamespace}/{locale}.json5")
                    .fileCodec(FileCodec.JSON5)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/nsA/de.json5",
            "roundtrip/json5/namespace_dir/nsA/de.json5");
        provideTranslationResource(wiring, "locales/nsB/de.json5",
            "roundtrip/json5/namespace_dir/nsB/de.json5");

        provideTranslationResource(wiring, "locales/nsA/en.json5",
            "roundtrip/json5/namespace_dir/nsA/en.json5");
        provideTranslationResource(wiring, "locales/nsB/en.json5",
            "roundtrip/json5/namespace_dir/nsB/en.json5");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}.json5")
                    .fileCodec(FileCodec.JSON5)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de.json5",
            "roundtrip/json5/single_dir/de.json5");
        provideTranslationResource(wiring, "locales/en.json5",
            "roundtrip/json5/single_dir/en.json5");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_file() throws IOException {
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

        provideTranslationResource(wiring, "locales/translations.json5",
            "roundtrip/json5/single_file/translations.json5");

        assertLosslessRoundtrip(wiring);
    }
}
