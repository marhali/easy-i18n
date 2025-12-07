package de.marhali.easyi18n.next_io.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author marhali
 */
public class TemplateParserTest {
    @Test
    public void test_empty_template_returns_empty_list() {
        Assert.assertEquals(
            new ArrayList<>(),
            TemplateParser.parseSegments("")
        );
    }

    @Test
    public void test_plaintext_template_returns_plaintext_segment() {
        Assert.assertEquals(
            new ArrayList<>(List.of(TemplateSegment.fromLiteral("myPlaintext"))),
            TemplateParser.parseSegments("myPlaintext")
        );
    }

    @Test
    public void test_empty_parameter_name_throws() {
        var ex = Assert.assertThrows(
            IllegalArgumentException.class,
            () -> TemplateParser.parseSegments("{}")
        );

        Assert.assertEquals(
            "Invalid parameter name '' in template '{}'. The value must match '^[A-Za-z0-9_]+$'",
            ex.getMessage()
        );
    }

    @Test
    public void test_unclosed_parameter_name_throws() {
        var ex = Assert.assertThrows(
            IllegalArgumentException.class,
            () -> TemplateParser.parseSegments("{mySeg{more")
        );

        Assert.assertEquals(
            "Missing closing parameter segment ('}') in template '{mySeg{more'",
            ex.getMessage()
        );
    }

    @Test
    public void test_invalid_parameter_name_throws() {
        var ex = Assert.assertThrows(
            IllegalArgumentException.class,
            () -> TemplateParser.parseSegments("{param-name}")
        );

        Assert.assertEquals(
            "Invalid parameter name 'param-name' in template '{param-name}'. The value must match '^[A-Za-z0-9_]+$'",
            ex.getMessage()
        );
    }

    @Test
    public void test_parameter_name_escaped_returns_plaintext_segment() {
        Assert.assertEquals(
            new ArrayList<>(List.of(TemplateSegment.fromLiteral("{escapedParameter}"))),
            TemplateParser.parseSegments("\\{escapedParameter}")
        );
    }

    @Test
    public void test_simple_parameter_template_returns_parameter_segment() {
        Assert.assertEquals(
            new ArrayList<>(List.of(TemplateSegment.fromParameter("mySegment", null,null))),
            TemplateParser.parseSegments("{mySegment}")
        );
    }

    @Test
    public void test_constraint_parameter_template_returns_constraint_segment() {
        Assert.assertEquals(
            new ArrayList<>(List.of(TemplateSegment.fromParameter("mySegment", null,"[^/]+"))),
            TemplateParser.parseSegments("{mySegment::[^/]+}")
        );
    }

    @Test
    public void test_all_variants_returns_multiple_segments() {
        Assert.assertEquals(
            new ArrayList<>(List.of(
                TemplateSegment.fromLiteral("some plaintext/{escapedParam}."),
                TemplateSegment.fromParameter("simpleParam", null,null),
                TemplateSegment.fromLiteral("/more plain/"),
                TemplateSegment.fromParameter("constraintParam", "myDelim","myRegex"),
                TemplateSegment.fromLiteral(".trail")
            )),
            TemplateParser.parseSegments("some plaintext/\\{escapedParam}.{simpleParam}/more plain/{constraintParam:myDelim:myRegex}.trail")
        );
    }
}
