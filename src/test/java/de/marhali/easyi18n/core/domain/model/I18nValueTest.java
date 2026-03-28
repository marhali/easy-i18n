package de.marhali.easyi18n.core.domain.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author marhali
 */
public class I18nValueTest {

    @Test
    public void testFromEscapedPreservesRaw() {
        I18nValue value = I18nValue.fromEscaped("hello\\nworld");
        Assert.assertEquals("hello\\nworld", value.raw());
    }

    @Test
    public void testFromUnescapedEscapesNewline() {
        I18nValue value = I18nValue.fromUnescaped("hello\nworld");
        Assert.assertEquals("hello\\nworld", value.raw());
    }

    @Test
    public void testFromUnescapedEscapesTab() {
        I18nValue value = I18nValue.fromUnescaped("hello\tworld");
        Assert.assertEquals("hello\\tworld", value.raw());
    }

    @Test
    public void testFromUnescapedEscapesCarriageReturn() {
        I18nValue value = I18nValue.fromUnescaped("hello\rworld");
        Assert.assertEquals("hello\\rworld", value.raw());
    }

    @Test
    public void testFromUnescapedEscapesBackslash() {
        I18nValue value = I18nValue.fromUnescaped("hello\\world");
        Assert.assertEquals("hello\\\\world", value.raw());
    }

    @Test
    public void testFromInputStringPreservesRaw() {
        I18nValue value = I18nValue.fromInputString("hello\\nworld");
        Assert.assertEquals("hello\\nworld", value.raw());
    }

    @Test
    public void testToInputStringReturnsRaw() {
        I18nValue value = I18nValue.fromEscaped("hello\\nworld");
        Assert.assertEquals("hello\\nworld", value.toInputString());
    }

    @Test
    public void testToUnescapedUnescapesNewline() {
        I18nValue value = I18nValue.fromEscaped("hello\\nworld");
        Assert.assertEquals("hello\nworld", value.toUnescaped());
    }

    @Test
    public void testToUnescapedUnescapesTab() {
        I18nValue value = I18nValue.fromEscaped("hello\\tworld");
        Assert.assertEquals("hello\tworld", value.toUnescaped());
    }

    @Test
    public void testRoundTripUnescapedToEscapedAndBack() {
        String original = "line1\nline2\ttabbed\r\nwindows";
        I18nValue value = I18nValue.fromUnescaped(original);
        Assert.assertEquals(original, value.toUnescaped());
    }

    @Test
    public void testPlainTextUnchanged() {
        I18nValue value = I18nValue.fromUnescaped("simple text");
        Assert.assertEquals("simple text", value.raw());
        Assert.assertEquals("simple text", value.toUnescaped());
    }
}
