package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author marhali
 */
public class I18nPropertiesResourcesRoundtripTest extends I18nResourcesRoundtripTestBase {

    @Test
    public void test_roundtrip_locale_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}/{pathNamespace}.properties")
                    .fileCodec(FileCodec.PROPERTIES)
                    .fileTemplate("[{fileKey:.}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de/nsA.properties",
            "roundtrip/properties/locale_dir/de/nsA.properties");
        provideTranslationResource(wiring, "locales/de/nsB.properties",
            "roundtrip/properties/locale_dir/de/nsB.properties");

        provideTranslationResource(wiring, "locales/en/nsA.properties",
            "roundtrip/properties/locale_dir/en/nsA.properties");
        provideTranslationResource(wiring, "locales/en/nsB.properties",
            "roundtrip/properties/locale_dir/en/nsB.properties");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_namespace_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{pathNamespace}/{locale}.properties")
                    .fileCodec(FileCodec.PROPERTIES)
                    .fileTemplate("[{fileKey:.}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/nsA/de.properties",
            "roundtrip/properties/namespace_dir/nsA/de.properties");
        provideTranslationResource(wiring, "locales/nsB/de.properties",
            "roundtrip/properties/namespace_dir/nsB/de.properties");

        provideTranslationResource(wiring, "locales/nsA/en.properties",
            "roundtrip/properties/namespace_dir/nsA/en.properties");
        provideTranslationResource(wiring, "locales/nsB/en.properties",
            "roundtrip/properties/namespace_dir/nsB/en.properties");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}.properties")
                    .fileCodec(FileCodec.PROPERTIES)
                    .fileTemplate("[{fileKey:.}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de.properties",
            "roundtrip/properties/single_dir/de.properties");
        provideTranslationResource(wiring, "locales/en.properties",
            "roundtrip/properties/single_dir/en.properties");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_file()  throws IOException {
        // Flat file does not provide element writing grouped by locale
        // Might work, if sorting is enabled and the Properties implementation uses a TreeMap
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

        provideTranslationResource(wiring, "locales/translations.properties",
            "roundtrip/properties/single_file/translations.properties");

        assertLosslessRoundtrip(wiring);
    }
}
