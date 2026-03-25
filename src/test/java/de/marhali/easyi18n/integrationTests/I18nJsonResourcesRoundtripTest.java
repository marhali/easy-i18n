package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author marhali
 */
public class I18nJsonResourcesRoundtripTest extends I18nResourcesRoundtripTestBase {

    @Test
    public void test_roundtrip_locale_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}/{pathNamespace}.json")
                    .fileCodec(FileCodec.JSON)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de/nsA.json",
            "roundtrip/json/locale_dir/de/nsA.json");
        provideTranslationResource(wiring, "locales/de/nsB.json",
            "roundtrip/json/locale_dir/de/nsB.json");

        provideTranslationResource(wiring, "locales/en/nsA.json",
            "roundtrip/json/locale_dir/en/nsA.json");
        provideTranslationResource(wiring, "locales/en/nsB.json",
            "roundtrip/json/locale_dir/en/nsB.json");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_namespace_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{pathNamespace}/{locale}.json")
                    .fileCodec(FileCodec.JSON)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/nsA/de.json",
            "roundtrip/json/namespace_dir/nsA/de.json");
        provideTranslationResource(wiring, "locales/nsB/de.json",
            "roundtrip/json/namespace_dir/nsB/de.json");

        provideTranslationResource(wiring, "locales/nsA/en.json",
            "roundtrip/json/namespace_dir/nsA/en.json");
        provideTranslationResource(wiring, "locales/nsB/en.json",
            "roundtrip/json/namespace_dir/nsB/en.json");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}.json")
                    .fileCodec(FileCodec.JSON)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de.json",
            "roundtrip/json/single_dir/de.json");
        provideTranslationResource(wiring, "locales/en.json",
            "roundtrip/json/single_dir/en.json");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_file()  throws IOException {
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

        provideTranslationResource(wiring, "locales/translations.json",
            "roundtrip/json/single_file/translations.json");

        assertLosslessRoundtrip(wiring);
    }
}
