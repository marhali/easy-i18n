package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.io.parser.yaml.YamlArrayMapper;
import de.marhali.easyi18n.io.parser.yaml.YamlMapper;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.KeyPath;
import org.apache.commons.lang.StringEscapeUtils;

import org.junit.Assert;

import java.util.*;

/**
 * Unit tests for {@link YamlMapper}.
 * @author marhali
 */
@SuppressWarnings("unchecked")
public class YamlMapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        Map<String, Object> input = new HashMap<>();
        input.put("zulu", "test");
        input.put("alpha", "test");
        input.put("bravo", "test");

        TranslationData data = new TranslationData(false);
        YamlMapper.read("en", input, data.getRootNode());

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), true);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("zulu", "alpha", "bravo"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testSorting() {
        Map<String, Object> input = new HashMap<>();
        input.put("zulu", "test");
        input.put("alpha", "test");
        input.put("bravo", "test");

        TranslationData data = new TranslationData(true);
        YamlMapper.read("en", input, data.getRootNode());

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("simple"), create(arraySimple));
        data.setTranslation(new KeyPath("escaped"), create(arrayEscaped));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.get("simple") instanceof List);
        Assert.assertEquals(arraySimple, YamlArrayMapper.read((List<Object>) output.get("simple")));
        Assert.assertTrue(output.get("escaped") instanceof List);
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(YamlArrayMapper.read((List<Object>) output.get("escaped"))));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertTrue(YamlArrayMapper.isArray(input.getTranslation(new KeyPath("simple")).get("en")));
        Assert.assertTrue(YamlArrayMapper.isArray(input.getTranslation(new KeyPath("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("chars"), create(specialCharacters));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(specialCharacters, output.get("chars"));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters,
                StringEscapeUtils.unescapeJava(input.getTranslation(new KeyPath("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("nested", "key", "section"), create("test"));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.containsKey("nested"));
        Assert.assertTrue(((Map<String, Object>) output.get("nested")).containsKey("key"));

        Assert.assertEquals("test", ((Map)((Map)output.get("nested")).get("key")).get("section"));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("nested", "key", "section")).get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("long.key.with.many.sections"), create("test"));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.containsKey("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("space"), create(leadingSpace));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(leadingSpace, output.get("space"));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation(new KeyPath("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("numbered"), create("+90d"));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(90.0, output.get("numbered"));

        Map<String, Object> input = new HashMap<>();
        input.put("numbered", 143.23);
        YamlMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }

    @Override
    public void testNumbersAsStrings() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("stringNumbered"), create("+90d"));

        Map<String, Object> output = new HashMap<>();
        YamlMapper.write("en", output, data.getRootNode(), true);

        Assert.assertEquals("+90d", output.get("stringNumbered"));

        Map<String, Object> input = new HashMap<>();
        input.put("numbered", 143.23);
        YamlMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }
}