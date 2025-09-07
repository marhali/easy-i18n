package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.model.TranslationValue;

import org.junit.Test;

/**
 * Defines test cases for {@link de.marhali.easyi18n.model.TranslationNode} mapping.
 * @author marhali
 */
public abstract class AbstractMapperTest {

    protected final String specialCharacters = "Special characters: äü@Öä€/$§;.-?+~#```'' end";
    protected final String arraySimple = "!arr[first;second]";
    protected final String arrayEscaped = "!arr[first\\;element;second element;third\\;element]";
    protected final String leadingSpace = " leading space";

    @Test
    public abstract void testNonSorting();

    @Test
    public abstract void testSorting();

    @Test
    public abstract void testArrays();

    @Test
    public abstract void testSpecialCharacters();

    @Test
    public abstract void testNestedKeys();

    @Test
    public abstract void testNonNestedKeys();

    @Test
    public abstract void testLeadingSpace();

    @Test
    public abstract void testNumbers();

    @Test
    public abstract void testNumbersAsStrings();

    protected TranslationValue create(String content) {
        return new TranslationValue("en", content);
    }
}