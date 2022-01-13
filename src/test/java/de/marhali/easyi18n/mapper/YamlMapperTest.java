package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.io.yaml.YamlArrayMapper;
import de.marhali.easyi18n.io.yaml.YamlMapper;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;
import org.apache.commons.lang.StringEscapeUtils;

import org.junit.Assert;

import thito.nodeflow.config.MapSection;
import thito.nodeflow.config.Section;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit tests for {@link YamlMapper}.
 * @author marhali
 */
public class YamlMapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        Section input = new MapSection();
        input.set("zulu", "test");
        input.set("alpha", "test");
        input.set("bravo", "test");

        TranslationData data = new TranslationData(false);
        YamlMapper.read("en", input, data.getRootNode());

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("zulu", "alpha", "bravo"));
        Assert.assertEquals(expect, output.getKeys());
    }

    @Override
    public void testSorting() {
        Section input = new MapSection();
        input.set("zulu", "test");
        input.set("alpha", "test");
        input.set("bravo", "test");

        TranslationData data = new TranslationData(true);
        YamlMapper.read("en", input, data.getRootNode());

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.getKeys());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("simple"), create(arraySimple));
        data.setTranslation(KeyPath.of("escaped"), create(arrayEscaped));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.isList("simple"));
        Assert.assertEquals(arraySimple, YamlArrayMapper.read(output.getList("simple").get()));
        Assert.assertTrue(output.isList("escaped"));
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(YamlArrayMapper.read(output.getList("escaped").get())));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertTrue(YamlArrayMapper.isArray(input.getTranslation(KeyPath.of("simple")).get("en")));
        Assert.assertTrue(YamlArrayMapper.isArray(input.getTranslation(KeyPath.of("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("chars"), create(specialCharacters));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(specialCharacters, output.getString("chars").get());

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters,
                StringEscapeUtils.unescapeJava(input.getTranslation(KeyPath.of("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("nested", "key", "section"), create("test"));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertEquals("test", output.getString("nested.key.section").get());

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(KeyPath.of("nested", "key", "section")).get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("long.key.with.many.sections"), create("test"));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.getKeys().contains("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(KeyPath.of("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("space"), create(leadingSpace));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(leadingSpace, output.getString("space").get());

        TranslationData input = new TranslationData(true);
        YamlMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation(KeyPath.of("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("numbered"), create("15000"));

        Section output = new MapSection();
        YamlMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(15000, output.getInteger("numbered").get().intValue());

        Section input = new MapSection();
        input.set("numbered", 143.23);
        YamlMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(KeyPath.of("numbered")).get("en"));
    }
}