package de.marhali.easyi18n.mapper;

import com.google.gson.JsonObject;

import com.google.gson.JsonPrimitive;

import de.marhali.easyi18n.io.parser.json.JsonArrayMapper;
import de.marhali.easyi18n.io.parser.json.JsonMapper;
import de.marhali.easyi18n.model.TranslationData;

import de.marhali.easyi18n.model.KeyPath;
import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Assert;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit tests for {@link JsonMapper}.
 * @author marhali
 */
public class JsonMapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        JsonObject input = new JsonObject();
        input.add("zulu", new JsonPrimitive("test"));
        input.add("alpha", new JsonPrimitive("test"));
        input.add("bravo", new JsonPrimitive("test"));

        TranslationData data = new TranslationData(false);
        JsonMapper.read("en", input, data.getRootNode());

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("zulu", "alpha", "bravo"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testSorting() {
        JsonObject input = new JsonObject();
        input.add("zulu", new JsonPrimitive("test"));
        input.add("alpha", new JsonPrimitive("test"));
        input.add("bravo", new JsonPrimitive("test"));

        TranslationData data = new TranslationData(true);
        JsonMapper.read("en", input, data.getRootNode());

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("simple"), create(arraySimple));
        data.setTranslation(new KeyPath("escaped"), create(arrayEscaped));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.get("simple").isJsonArray());
        Assert.assertEquals(arraySimple, JsonArrayMapper.read(output.get("simple").getAsJsonArray()));
        Assert.assertTrue(output.get("escaped").isJsonArray());
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(JsonArrayMapper.read(output.get("escaped").getAsJsonArray())));

        TranslationData input = new TranslationData(true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(new KeyPath("simple")).get("en")));
        Assert.assertTrue(JsonArrayMapper.isArray(input.getTranslation(new KeyPath("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("chars"), create(specialCharacters));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(specialCharacters, output.get("chars").getAsString());

        TranslationData input = new TranslationData(true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters,
                StringEscapeUtils.unescapeJava(input.getTranslation(new KeyPath("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("nested", "key", "section"), create("test"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals("test", output.getAsJsonObject("nested").getAsJsonObject("key").get("section").getAsString());

        TranslationData input = new TranslationData(true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("nested", "key", "section")).get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("long.key.with.many.sections"), create("test"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertTrue(output.has("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation(new KeyPath("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("space"), create(leadingSpace));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(leadingSpace, output.get("space").getAsString());

        TranslationData input = new TranslationData(true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation(new KeyPath("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("numbered"), create("+90d"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), false);

        Assert.assertEquals(90.0, output.get("numbered").getAsNumber());

        JsonObject input = new JsonObject();
        input.addProperty("numbered", 143.23);
        JsonMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }

    @Override
    public void testNumbersAsStrings() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("stringNumbered"), create("+90d"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode(), true);

        Assert.assertEquals("+90d", output.get("stringNumbered").getAsString());

        JsonObject input = new JsonObject();
        input.addProperty("numbered", 143.23);
        JsonMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));

    }
}