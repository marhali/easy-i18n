package de.marhali.easyi18n.integrationTests;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author marhali
 */
public class I18nYamlResourcesRoundtripTest extends I18nResourcesRoundtripTestBase {

    @Test
    public void test_roundtrip_locale_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}/{pathNamespace}.yaml")
                    .fileCodec(FileCodec.YAML)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de/nsA.yaml",
            "roundtrip/yaml/locale_dir/de/nsA.yaml");
        provideTranslationResource(wiring, "locales/de/nsB.yaml",
            "roundtrip/yaml/locale_dir/de/nsB.yaml");

        provideTranslationResource(wiring, "locales/en/nsA.yaml",
            "roundtrip/yaml/locale_dir/en/nsA.yaml");
        provideTranslationResource(wiring, "locales/en/nsB.yaml",
            "roundtrip/yaml/locale_dir/en/nsB.yaml");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_namespace_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{pathNamespace}/{locale}.yaml")
                    .fileCodec(FileCodec.YAML)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{pathNamespace}:{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/nsA/de.yaml",
            "roundtrip/yaml/namespace_dir/nsA/de.yaml");
        provideTranslationResource(wiring, "locales/nsB/de.yaml",
            "roundtrip/yaml/namespace_dir/nsB/de.yaml");

        provideTranslationResource(wiring, "locales/nsA/en.yaml",
            "roundtrip/yaml/namespace_dir/nsA/en.yaml");
        provideTranslationResource(wiring, "locales/nsB/en.yaml",
            "roundtrip/yaml/namespace_dir/nsB/en.yaml");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_dir() throws IOException {
        var config = projectConfigBuilder
            .modules(List.of(
                moduleConfigBuilder
                    .pathTemplate("locales/{locale}.yaml")
                    .fileCodec(FileCodec.YAML)
                    .fileTemplate("[{fileKey}]")
                    .keyTemplate("{fileKey:.}")
                    .build()
            )).build();

        var wiring = new IntegrationTestWiring();

        wiring.projectConfig.updateProjectConfig(config);

        provideTranslationResource(wiring, "locales/de.yaml",
            "roundtrip/yaml/single_dir/de.yaml");
        provideTranslationResource(wiring, "locales/en.yaml",
            "roundtrip/yaml/single_dir/en.yaml");

        assertLosslessRoundtrip(wiring);
    }

    @Test
    public void test_roundtrip_single_file()  throws IOException {
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

        provideTranslationResource(wiring, "locales/translations.yaml",
            "roundtrip/yaml/single_file/translations.yaml");

        assertLosslessRoundtrip(wiring);
    }
}
