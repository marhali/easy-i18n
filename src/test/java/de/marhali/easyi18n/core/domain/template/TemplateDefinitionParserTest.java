package de.marhali.easyi18n.core.domain.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author marhali
 */
public class TemplateDefinitionParserTest {
    @Test
    public void test_empty_template_returns_empty_list() {
        Assert.assertEquals(
            new Template("", List.of()),
            TemplateDefinitionParser.parse("")
        );
    }

    @Test
    public void test_plaintext_template_returns_plaintext_segment() {
        Assert.assertEquals(
            new Template("myPlaintext", List.of(TemplateElement.fromLiteral("myPlaintext"))),
            TemplateDefinitionParser.parse("myPlaintext")
        );
    }

    @Test
    public void test_empty_parameter_name_throws() {
        var ex = Assert.assertThrows(IllegalArgumentException.class,
            () -> TemplateDefinitionParser.parse("{}"));

        Assert.assertEquals(
            "Invalid parameter name '' in template '{}'. The value must match '^[A-Za-z0-9_]+$'",
            ex.getMessage()
        );
    }

    @Test
    public void test_unclosed_parameter_name_throws() {
        var ex = Assert.assertThrows(IllegalArgumentException.class,
            () -> TemplateDefinitionParser.parse("{mySeg{more"));

        Assert.assertEquals(
            "Missing closing parameter segment ('}') in template '{mySeg{more'",
            ex.getMessage()
        );
    }

    @Test
    public void test_invalid_parameter_name_throws() {
        var ex = Assert.assertThrows(IllegalArgumentException.class,
            () -> TemplateDefinitionParser.parse("{param-name}"));

        Assert.assertEquals(
            "Invalid parameter name 'param-name' in template '{param-name}'. The value must match '^[A-Za-z0-9_]+$'",
            ex.getMessage()
        );
    }

    @Test
    public void test_parameter_name_escaped_returns_plaintext_segment() {
        Assert.assertEquals(
            new Template("\\{escapedParameter}", List.of(TemplateElement.fromLiteral("{escapedParameter}"))),
            TemplateDefinitionParser.parse("\\{escapedParameter}")
        );
    }

    @Test
    public void test_simple_parameter_template_returns_parameter_segment() {
        Assert.assertEquals(
            new Template("{mySegment}", List.of(TemplateElement.fromPlaceholder("mySegment", null, null))),
            TemplateDefinitionParser.parse("{mySegment}")
        );
    }

    @Test
    public void test_constraint_parameter_template_returns_constraint_segment() {
        Assert.assertEquals(
            new Template("{mySegment::[^/]+}", List.of(TemplateElement.fromPlaceholder("mySegment", null, "[^/]+"))),
            TemplateDefinitionParser.parse("{mySegment::[^/]+}")
        );
    }

    @Test
    public void test_all_variants_returns_multiple_segments() {
        Assert.assertEquals(
            new Template(
                "some plaintext/\\{escapedParam}.{simpleParam}/more plain/{constraintParam:myDelim:myRegex}.trail",
                List.of(
                    TemplateElement.fromLiteral("some plaintext/{escapedParam}."),
                    TemplateElement.fromPlaceholder("simpleParam", null, null),
                    TemplateElement.fromLiteral("/more plain/"),
                    TemplateElement.fromPlaceholder("constraintParam", "myDelim", "myRegex"),
                    TemplateElement.fromLiteral(".trail"))),
            TemplateDefinitionParser.parse(
                "some plaintext/\\{escapedParam}.{simpleParam}/more plain/{constraintParam:myDelim:myRegex}.trail"
            )
        );
    }
}
