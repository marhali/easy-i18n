package de.marhali.easyi18n.next_io.path;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author marhali
 */
public class PathTemplateTest {
    @Test
    public void test_null_template_throws() {
        var ex = Assert.assertThrows(
            NullPointerException.class,
            () -> PathTemplate.compile(null)
        );

        Assert.assertEquals(
            "template must not be null",
            ex.getMessage()
        );
    }

    @Test
    public void test_match_unknown_path_returns_null() {
        var template = PathTemplate.compile("myPath");

        Assert.assertNull(template.match("anyOtherPath"));
    }

    @Test
    public void test_stable_match_and_build_roundtrip() {
        var template = PathTemplate.compile("$PROJECT_DIR$/{ns}/{locale}.{fileExt}");

        String inputPath = "$PROJECT_DIR$/account/de.json";

        var params = template.match("$PROJECT_DIR$/account/de.json");

        Assert.assertEquals(
            Map.of("ns", "account", "locale", "de", "fileExt", "json"),
            params
        );

        Assert.assertEquals(inputPath, template.build(params));
    }
}
