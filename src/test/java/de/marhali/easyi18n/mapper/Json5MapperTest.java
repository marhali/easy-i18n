package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.io.parser.json.JsonArrayMapper;
import de.marhali.easyi18n.io.parser.json5.Json5ArrayMapper;
import de.marhali.easyi18n.io.parser.json5.Json5Mapper;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.json5.Json5Object;
import de.marhali.json5.Json5Primitive;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Assert;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit tests for {@link Json5Mapper}.
 * @author marhali
 */
public class Json5MapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        Json5Object input = new Json5Object();
        input.add("zulu", Json5Primitive.of("test"));
        input.add("alpha", Json5Primitive.of("test"));
        input.add("bravo", Json5Primitive.of("test"));

        TranslationData data = new TranslationData(false);
        Json5Mapper.read("en", input, data.getRootNode());

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("zulu", "alpha", "bravo"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testSorting() {
        Json5Object input = new Json5Object();
        input.add("zulu", Json5Primitive.of("test"));
        input.add("alpha", Json5Primitive.of("test"));
        input.add("bravo", Json5Primitive.of("test"));

        TranslationData data = new TranslationData(false);
        Json5Mapper.read("en", input, data.getRootNode());

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("simple"), create(arraySimple));
        data.setTranslation(new KeyPath("escaped"), create(arrayEscaped));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.get("simple").isJson5Array());
        Assert.assertEquals(arraySimple, Json5ArrayMapper.read(output.get("simple").getAsJson5Array()));
        Assert.assertTrue(output.get("escaped").isJson5Array());
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(Json5ArrayMapper.read(output.get("escaped").getAsJson5Array())));

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(new KeyPath("simple")).get("en")));
        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(new KeyPath("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("chars"), create(specialCharacters));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(specialCharacters, output.get("chars").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters,
                StringEscapeUtils.unescapeJava(input.getTranslation(new KeyPath("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("nested", "key", "section"), create("test"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals("test", output.getAsJson5Object("nested").getAsJson5Object("key").get("section").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("nested", "key", "section")).get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("long.key.with.many.sections"), create("test"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.has("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("space"), create(leadingSpace));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(leadingSpace, output.get("space").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation(new KeyPath("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("numbered"), create("+90d"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(90.0, output.get("numbered").getAsNumber());

        Json5Object input = new Json5Object();
        input.addProperty("numbered", 143.23);
        Json5Mapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }

    @Override
    public void testNumbersAsStrings() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("stringNumbered"), create("+90d"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode(), true);

        Assert.assertEquals("+90d", output.get("stringNumbered").getAsString());

        Json5Object input = new Json5Object();
        input.addProperty("numbered", 143.23);
        Json5Mapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }
}