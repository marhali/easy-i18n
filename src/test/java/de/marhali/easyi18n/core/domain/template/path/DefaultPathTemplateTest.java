package de.marhali.easyi18n.core.domain.template.path;

import de.marhali.easyi18n.core.domain.model.I18nBuiltinParam;
import de.marhali.easyi18n.core.domain.model.I18nParams;
import de.marhali.easyi18n.core.domain.model.I18nPath;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;


/**
 * @author marhali
 */
public class DefaultPathTemplateTest {
    @Test
    public void test_match_unknown_path_returns_null() {
        var template = DefaultPathTemplate.compile("myPath.json");

        Assert.assertNull(template.matchCanonical("anyOtherPath.json"));
    }

    @Test
    public void test_stable_match_and_build_roundtrip() {
        var template = DefaultPathTemplate.compile("$PROJECT_DIR$/{ns}/{locale}.json");

        String inputPath = "$PROJECT_DIR$/account/de.json";

        var params = template.matchCanonical(inputPath);

        var expectedParams = I18nParams.builder()
            .add("ns", "account")
            .add(I18nBuiltinParam.LOCALE, "de")
            .build();

        Assert.assertEquals(
            expectedParams,
            params
        );

        assert params != null;

        var variants = template.buildVariants(params);

        Assert.assertEquals(
            Set.of(
                new I18nPath(inputPath, expectedParams)
            ),
            variants
        );
    }
}
