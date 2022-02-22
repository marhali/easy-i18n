package de.marhali.easyi18n.mapper;

import com.google.gson.JsonObject;
import de.marhali.easyi18n.io.parser.json.JsonArrayMapper;
import de.marhali.easyi18n.io.parser.json.JsonMapper;
import de.marhali.easyi18n.io.parser.json5.Json5ArrayMapper;
import de.marhali.easyi18n.io.parser.json5.Json5Mapper;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;
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
        Json5Mapper.write("en", output, data.getRootNode());

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
        Json5Mapper.write("en", output, data.getRootNode());

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("simple"), create(arraySimple));
        data.setTranslation(KeyPath.of("escaped"), create(arrayEscaped));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.get("simple").isJsonArray());
        Assert.assertEquals(arraySimple, Json5ArrayMapper.read(output.get("simple").getAsJsonArray()));
        Assert.assertTrue(output.get("escaped").isJsonArray());
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(Json5ArrayMapper.read(output.get("escaped").getAsJsonArray())));

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(KeyPath.of("simple")).get("en")));
        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(KeyPath.of("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("chars"), create(specialCharacters));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertEquals(specialCharacters, output.get("chars").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters,
                StringEscapeUtils.unescapeJava(input.getTranslation(KeyPath.of("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("nested", "key", "section"), create("test"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertEquals("test", output.getAsJson5Object("nested").getAsJson5Object("key").get("section").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(KeyPath.of("nested", "key", "section")).get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("long.key.with.many.sections"), create("test"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.has("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(KeyPath.of("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("space"), create(leadingSpace));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertEquals(leadingSpace, output.get("space").getAsString());

        TranslationData input = new TranslationData(true);
        Json5Mapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation(KeyPath.of("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(KeyPath.of("numbered"), create("15000"));

        Json5Object output = new Json5Object();
        Json5Mapper.write("en", output, data.getRootNode());

        Assert.assertEquals(15000, output.get("numbered").getAsNumber());

        Json5Object input = new Json5Object();
        input.addProperty("numbered", 143.23);
        Json5Mapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(KeyPath.of("numbered")).get("en"));
    }
}