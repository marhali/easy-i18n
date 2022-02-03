package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.io.parser.properties.PropertiesArrayMapper;
import de.marhali.easyi18n.io.parser.properties.PropertiesMapper;
import de.marhali.easyi18n.io.parser.properties.SortableProperties;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Assert;

import java.util.*;

/**
 * Unit tests for {@link PropertiesMapper}.
 * @author marhali
 */
public class PropertiesMapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        SortableProperties input = new SortableProperties(false);
        input.setProperty("zulu", "test");
        input.setProperty("alpha", "test");
        input.setProperty("bravo", "test");

        TranslationData data = new TranslationData(false);
        PropertiesMapper.read("en", input, data);

        SortableProperties output = new SortableProperties(false);
        PropertiesMapper.write("en", output, data);

        List<String> expect = Arrays.asList("zulu", "alpha", "bravo");
        Assert.assertEquals(expect, new ArrayList<>(output.keySet()));
    }

    @Override
    public void testSorting() {
        SortableProperties input = new SortableProperties(true);
        input.setProperty("zulu", "test");
        input.setProperty("alpha", "test");
        input.setProperty("bravo", "test");

        TranslationData data = new TranslationData(true);
        PropertiesMapper.read("en", input, data);

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        List<String> expect = Arrays.asList("alpha", "bravo", "zulu");
        Assert.assertEquals(expect, new ArrayList<>(output.keySet()));
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("simple"), create(arraySimple));
        data.setTranslation(KeyPath.of("escaped"), create(arrayEscaped));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertTrue(output.get("simple") instanceof String[]);
        Assert.assertEquals(arraySimple, PropertiesArrayMapper.read((String[]) output.get("simple")));
        Assert.assertTrue(output.get("escaped") instanceof String[]);
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(PropertiesArrayMapper.read((String[]) output.get("escaped"))));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input);

        Assert.assertTrue(PropertiesArrayMapper.isArray(input.getTranslation(KeyPath.of("simple")).get("en")));
        Assert.assertTrue(PropertiesArrayMapper.isArray(input.getTranslation(KeyPath.of("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("chars"), create(specialCharacters));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertEquals(specialCharacters, output.get("chars"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input);

        Assert.assertEquals(specialCharacters, StringEscapeUtils.unescapeJava(input.getTranslation(KeyPath.of("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("nested", "key", "sections"), create("test"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertEquals("test", output.get("nested.key.sections"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input);

        System.out.println(input);

        Assert.assertTrue(input.getRootNode().getChildren().containsKey("nested"));
        Assert.assertEquals("test", input.getTranslation(KeyPath.of("nested", "key", "sections")).get("en"));
    }

    @Override
    public void testNonNestedKeys() { // Note: Key nesting is not supported in properties file.
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("long.key.with.many.sections"), create("test"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertNotNull(output.get("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input);

        Assert.assertEquals("test", input.getTranslation(KeyPath.of("long", "key", "with", "many", "sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("space"), create(leadingSpace));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertEquals(leadingSpace, output.get("space"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input);

        Assert.assertEquals(leadingSpace, input.getTranslation(KeyPath.of("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("numbered"), create("15000"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data);

        Assert.assertEquals(15000, output.get("numbered"));

        SortableProperties input = new SortableProperties(true);
        input.put("numbered", 143.23);
        PropertiesMapper.read("en", input, data);

        Assert.assertEquals("143.23", data.getTranslation(KeyPath.of("numbered")).get("en"));
    }
}