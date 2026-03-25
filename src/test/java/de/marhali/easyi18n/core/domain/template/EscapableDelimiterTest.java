package de.marhali.easyi18n.core.domain.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author marhali
 */
public class EscapableDelimiterTest {

    @Test
    public void testJoin_nullDelimiter_concatenatesWithoutEscaping() {
        assertJoinEquals(L("a", "b", "c"), null, "abc");
        assertSplitEquals("abc", null, List.of("abc"));
    }

    @Test
    public void testJoin_emptyDelimiter_concatenatesWithoutEscaping() {
        assertJoinEquals(L("a", "b", "c"), "", "abc");
        assertSplitEquals("abc", "", List.of("abc"));
    }

    @Test
    public void testJoin_simpleDelimiter_noEscapes() {
        assertJoinEquals(L("a", "b", "c"), ",", "a,b,c");
    }

    @Test
    public void testJoin_escapesDelimiterInValue() {
        assertJoinEquals(L("a,b", "c"), ",", "a\\,b,c");
    }

    @Test
    public void testJoin_escapesBackslashInValue() {
        assertJoinEquals(L("a\\b"), ",", "a\\\\b");
    }

    @Test
    public void testJoin_escapesBackslashAndDelimiterInSameValue() {
        assertJoinEquals(L("a\\b,c"), ",", "a\\\\b\\,c");
    }

    @Test
    public void testJoin_multiCharDelimiter_escapesDelimiter() {
        assertJoinEquals(L("a||b", "c"), "||", "a\\||b||c");
    }

    @Test
    public void testJoin_emptyValues_returnsEmptyString() {
        assertJoinEquals(List.of(), ",", "");
        assertSplitEquals("", ",", List.of(""));
    }

    @Test
    public void testSplit_nullDelimiter_returnsSingleElement() {
        assertSplitEquals("a,b,c", null, List.of("a,b,c"));
    }

    @Test
    public void testSplit_emptyDelimiter_returnsSingleElement() {
        assertSplitEquals("a,b,c", "", List.of("a,b,c"));
    }

    @Test
    public void testSplit_emptyInput_withDelimiter_returnsSingletonEmpty() {
        assertSplitEquals("", ",", List.of(""));
    }

    @Test
    public void testSplit_simpleDelimiter_splits() {
        assertSplitEquals("a,b,c", ",", L("a", "b", "c"));
    }

    @Test
    public void testSplit_consecutiveAndEdgeDelimiters_produceEmptyTokens() {
        assertSplitEquals(",a,,b,", ",", L("", "a", "", "b", ""));
    }

    @Test
    public void testSplit_escapedDelimiter_branch() {
        assertSplitEquals("foo\\,bar,baz", ",", L("foo,bar", "baz"));
    }

    @Test
    public void testSplit_doubleEscape_branch() {
        assertSplitEquals("foo\\\\,bar", ",", L("foo\\", "bar"));
    }

    @Test
    public void testSplit_genericEscape_branch() {
        assertSplitEquals("a\\xb;c", ";", L("axb", "c"));
        assertSplitEquals("1\\+1;2", ";", L("1+1", "2"));
    }

    @Test
    public void testSplit_trailingEscape_branch() {
        assertSplitEquals("a\\", ",", List.of("a\\"));
    }

    @Test
    public void testSplit_unescapedDelimiter_branch_withMultiCharDelimiter() {
        assertSplitEquals("a||b||c", "||", L("a", "b", "c"));
    }

    @Test
    public void testSplit_escapedDelimiter_branch_withMultiCharDelimiter() {
        assertSplitEquals("a\\||b||c", "||", L("a||b", "c"));
    }

    @Test
    public void testSplit_genericEscape_branch_withMultiCharDelimiter() {
        assertSplitEquals("a\\|b||c", "||", L("a|b", "c"));
    }

    @Test
    public void testRoundTrip_preservesEmptyStrings() {
        assertRoundTrip(L("", "a", ""), ",");
        assertRoundTrip(L("", "", ""), ",");
    }

    @Test
    public void testRoundTrip_commas_various() {
        assertRoundTrip(L("a", "b", "c"), ",");
        assertRoundTrip(L("a,b", "c"), ",");
        assertRoundTrip(L("a\\b", "c"), ",");
        assertRoundTrip(L("a\\b,c", "x,y", "z"), ",");
        assertRoundTrip(L("a\\,b", "c"), ",");
    }

    @Test
    public void testRoundTrip_multiCharDelimiter_various() {
        assertRoundTrip(L("a", "b", "c"), "||");
        assertRoundTrip(L("a||b", "c"), "||");
        assertRoundTrip(L("a\\b", "c||d", "e"), "||");
    }

    private static List<String> L(String... items) {
        return Arrays.asList(items);
    }

    private static void assertJoinEquals(List<String> values, String delimiter, String expected) {
        String actual = EscapableDelimiter.joinByDelimiter(values, delimiter);
        Assert.assertEquals(expected, actual);
    }

    private static void assertSplitEquals(String input, String delimiter, List<String> expected) {
        List<String> actual = EscapableDelimiter.splitByDelimiter(input, delimiter);
        Assert.assertEquals(expected, actual);
    }

    private static void assertRoundTrip(List<String> values, String delimiter) {
        String joined = EscapableDelimiter.joinByDelimiter(values, delimiter);
        List<String> split = EscapableDelimiter.splitByDelimiter(joined, delimiter);
        Assert.assertEquals(values, split);
    }
}
