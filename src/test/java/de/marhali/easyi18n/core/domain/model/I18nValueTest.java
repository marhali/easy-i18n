package de.marhali.easyi18n.core.domain.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author marhali
 */
public class I18nValueTest {
    @Test
    public void test_fromBarePrimitive_returns_Bare() {
        var primitive = I18nValue.fromBarePrimitive(" my bare primitive ");

        Assert.assertFalse(primitive.isArray());
        Assert.assertTrue(primitive.isPrimitive());
        Assert.assertTrue(primitive.isBare());
        Assert.assertFalse(primitive.isQuoted());
        Assert.assertEquals(" my bare primitive ", primitive.getText());
    }

    @Test
    public void test_fromQuotedPrimitive_returns_Quoted() {
        var primitive = I18nValue.fromQuotedPrimitive(" my quoted primitive ");

        Assert.assertFalse(primitive.isArray());
        Assert.assertTrue(primitive.isPrimitive());
        Assert.assertFalse(primitive.isBare());
        Assert.assertTrue(primitive.isQuoted());
        Assert.assertEquals(" my quoted primitive ", primitive.getText());
    }

    @Test
    public void test_fromArray_returns_Array() {
        var array = I18nValue.fromArray(
            I18nValue.fromBarePrimitive(" my bare primitive "),
            I18nValue.fromQuotedPrimitive(" my quoted primitive ")
        );

        Assert.assertTrue(array.isArray());
        Assert.assertFalse(array.isPrimitive());
        Assert.assertArrayEquals(new I18nValue.Primitive[]{
            I18nValue.fromBarePrimitive(" my bare primitive "),
            I18nValue.fromQuotedPrimitive(" my quoted primitive ")
        }, array.elements());
    }

    @Test
    public void test_bare_toInputString() {
        Assert.assertEquals(
            "my bare text",
            I18nValue.fromBarePrimitive("my bare text").toInputString()
        );
    }

    @Test
    public void test_quoted_toInputString() {
        Assert.assertEquals(
            "\"my quoted text\"",
            I18nValue.fromQuotedPrimitive("my quoted text").toInputString()
        );
    }

    @Test
    public void test_array_toInputString() {
        Assert.assertEquals(
            "[my bare text; \"my quoted text\"]",
            I18nValue.fromArray(
                I18nValue.fromBarePrimitive("my bare text"),
                I18nValue.fromQuotedPrimitive("my quoted text")
            ).toInputString()
        );
    }

    @Test
    public void test_fromInputString_returns_Bare() {
        Assert.assertEquals(
            I18nValue.fromBarePrimitive("my bare text"),
            I18nValue.fromInputString("my bare text")
        );
    }

    @Test
    public void test_fromInputString_returns_Quoted() {
        Assert.assertEquals(
            I18nValue.fromQuotedPrimitive("my quoted text"),
            I18nValue.fromInputString("\"my quoted text\"")
        );
    }

    @Test
    public void test_fromInputString_returns_Array() {
        Assert.assertEquals(
            I18nValue.fromArray(
                I18nValue.fromBarePrimitive("my bare text"),
                I18nValue.fromQuotedPrimitive("my quoted text")
            ),
            I18nValue.fromInputString("[my bare text; \"my quoted text\"]")
            );
    }
}
