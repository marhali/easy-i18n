package de.marhali.easyi18n.next_io.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author marhali
 */
public class TemplatePatternTest {

    private static final String defaultParameterDelimiter = "myDelim";
    private static final String defaultParameterConstraint = "myRegexConstraint";

    private static void assertPatternEquals(Pattern expected, Pattern actual) {
        Assert.assertEquals(expected.pattern(), actual.pattern());
        Assert.assertEquals(expected.flags(), actual.flags());
    }

    @Test
    public void test_unknown_segments_throws() {
        var ex = Assert.assertThrows(
            UnsupportedOperationException.class,
            () -> TemplatePattern.fromSegments(List.of(new TemplateSegment()), defaultParameterConstraint)
        );

        Assert.assertEquals(
            "Unknown template segment: TemplateSegment",
            ex.getMessage()
        );
    }

    @Test
    public void test_empty_segments_returns_pattern() {
        assertPatternEquals(
            Pattern.compile("^$"),
            TemplatePattern.fromSegments(List.of(), defaultParameterConstraint)
        );
    }

    @Test
    public void test_literal_segment_returns_pattern() {
        assertPatternEquals(
            Pattern.compile("^" + Pattern.quote("my_Plaintext+") + "$"),
            TemplatePattern.fromSegments(List.of(TemplateSegment.fromLiteral("my_Plaintext+")), defaultParameterConstraint)
        );
    }

    @Test
    public void test_simple_parameter_segment_returns_pattern() {
        assertPatternEquals(
            Pattern.compile("^(?<myParam>" + defaultParameterConstraint + ")$"),
            TemplatePattern.fromSegments(List.of(TemplateSegment.fromParameter("myParam", null, null)), defaultParameterConstraint)
        );
    }

    @Test
    public void test_constraint_parameter_segment_returns_pattern() {
        assertPatternEquals(
            Pattern.compile("^(?<myParam>[^/]+)$"),
            TemplatePattern.fromSegments(List.of(TemplateSegment.fromParameter("myParam", null,"[^/]+")), defaultParameterConstraint)
        );
    }
}
