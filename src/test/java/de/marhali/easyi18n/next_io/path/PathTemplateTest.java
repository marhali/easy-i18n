package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_io.I18nBuiltinParam;
import de.marhali.easyi18n.next_io.I18nPath;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * @author marhali
 */
public class PathTemplateTest {

    @Test
    public void test_match_unknown_path_returns_null() {
        var template = PathTemplate.compile("myPath");

        Assert.assertNull(template.match("anyOtherPath"));
    }

    @Test
    public void test_stable_match_and_build_roundtrip() {
        var template = PathTemplate.compile("$PROJECT_DIR$/{ns}/{locale}.json");

        String inputPath = "$PROJECT_DIR$/account/de.json";

        var params = template.match("$PROJECT_DIR$/account/de.json");

        var expectedParams = I18nParams.builder()
            .add("ns", "account")
            .add(I18nBuiltinParam.LOCALE, "de")
            .build();

        Assert.assertEquals(
            expectedParams,
            params
        );

        assert params != null;

        Assert.assertEquals(
            Set.of(I18nPath.from(inputPath, I18nParams.builder()
                .add("ns", "account")
                .add(I18nBuiltinParam.LOCALE, "de")
                .build())),
            template.build(params));
    }
}
