package de.marhali.easyi18n.next_io.path;

import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_io.I18nBuiltinParam;
import de.marhali.easyi18n.next_io.template.TemplateParser;
import de.marhali.easyi18n.next_io.template.TemplateSegment;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author marhali
 */
public class PathBuilderTest {

    static List<TemplateSegment> segments = TemplateParser.parseSegments(
        "$PROJECT_DIR$/locales/{locale:[^/]+}/{pathNs:[^/]+}/{pathKey:[^.]+}.json"
    );

    static PathBuilder builder = new PathBuilder(segments);

    @Test
    public void test_build_throws_no_such_element_exception_if_param_key_missing() {
        var params = new I18nParams();

        var ex = Assert.assertThrows(NoSuchElementException.class, () -> builder.build(params));
        Assert.assertEquals("Missing values for parameter with name '" + I18nBuiltinParam.LOCALE.getParamName() + "'", ex.getMessage());
    }

    @Test
    public void test_build_throws_no_such_element_exception_if_param_has_no_values() {
        var params = new I18nParams();
        params.add(I18nBuiltinParam.LOCALE.getParamName(), List.of());

        var ex = Assert.assertThrows(NoSuchElementException.class, () -> builder.build(params));
        Assert.assertEquals("Missing values for parameter with name '" + I18nBuiltinParam.LOCALE.getParamName() + "'", ex.getMessage());
    }

    @Test
    public void test_path_parse_build_round_trip() {
        var params = new I18nParams();

        params.add(I18nBuiltinParam.LOCALE.getParamName(), "deDE", "enUS", "enGB");
        params.add("pathNs", "account", "billing");
        params.add("pathKey", "user");


        var paths = builder.build(params);

        var expectedPaths = Set.of(
            "$PROJECT_DIR$/locales/deDE/account/user.json",
            "$PROJECT_DIR$/locales/deDE/billing/user.json",
            "$PROJECT_DIR$/locales/enUS/account/user.json",
            "$PROJECT_DIR$/locales/enUS/billing/user.json",
            "$PROJECT_DIR$/locales/enGB/account/user.json",
            "$PROJECT_DIR$/locales/enGB/billing/user.json"
            );

        Assert.assertEquals(expectedPaths, paths);
    }
}
