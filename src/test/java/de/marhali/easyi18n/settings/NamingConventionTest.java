package de.marhali.easyi18n.settings;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import de.marhali.easyi18n.settings.presets.NamingConvention;

import java.io.IOException;

public class NamingConventionTest extends BasePlatformTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testConvertToNamingConvention() throws IOException {
        assertEquals("helloWorld", NamingConvention.convertKeyToConvention("Hello World", NamingConvention.CAMEL_CASE));
        assertEquals("hello_world", NamingConvention.convertKeyToConvention("Hello World", NamingConvention.SNAKE_CASE));
        assertEquals("HelloWorld", NamingConvention.convertKeyToConvention("Hello World", NamingConvention.PASCAL_CASE));
        assertEquals("HELLO_WORLD", NamingConvention.convertKeyToConvention("Hello World", NamingConvention.SNAKE_CASE_UPPERCASE));
    }

    public void testGetEnumNames() throws Exception {
        String[] expected = {"Camel Case", "Pascal Case", "Snake Case", "Snake Case (Uppercase)"};
        String[] actual = NamingConvention.getEnumNames();
        assertEquals(expected.length, actual.length);
    }


    public void testFromString() {
        assertEquals(NamingConvention.CAMEL_CASE, NamingConvention.fromString("Camel Case"));
        assertEquals(NamingConvention.PASCAL_CASE, NamingConvention.fromString("Pascal Case"));
        assertEquals(NamingConvention.SNAKE_CASE, NamingConvention.fromString("Snake Case"));
        assertEquals(NamingConvention.SNAKE_CASE_UPPERCASE, NamingConvention.fromString("Snake Case (Uppercase)"));
        assertEquals(NamingConvention.CAMEL_CASE, NamingConvention.fromString("Invalid Input"));
    }
}
