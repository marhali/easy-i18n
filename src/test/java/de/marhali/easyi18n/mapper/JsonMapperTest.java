package de.marhali.easyi18n.mapper;

import com.google.gson.JsonObject;

import com.google.gson.JsonPrimitive;

import de.marhali.easyi18n.io.json.JsonArrayMapper;
import de.marhali.easyi18n.io.json.JsonMapper;
import de.marhali.easyi18n.model.TranslationData;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Assert;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Unit tests for {@link de.marhali.easyi18n.io.json.JsonMapper}
 * @author marhali
 */
public class JsonMapperTest extends AbstractMapperTest {

    @Override
    public void testNonSorting() {
        JsonObject input = new JsonObject();
        input.add("zulu", new JsonPrimitive("test"));
        input.add("alpha", new JsonPrimitive("test"));
        input.add("bravo", new JsonPrimitive("test"));

        TranslationData data = new TranslationData(false, true);
        JsonMapper.read("en", input, data.getRootNode());

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("zulu", "alpha", "bravo"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testSorting() {
        JsonObject input = new JsonObject();
        input.add("zulu", new JsonPrimitive("test"));
        input.add("alpha", new JsonPrimitive("test"));
        input.add("bravo", new JsonPrimitive("test"));

        TranslationData data = new TranslationData(true, true);
        JsonMapper.read("en", input, data.getRootNode());

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Set<String> expect = new LinkedHashSet<>(Arrays.asList("alpha", "bravo", "zulu"));
        Assert.assertEquals(expect, output.keySet());
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true, true);
        data.setTranslation("simple", create(arraySimple));
        data.setTranslation("escaped", create(arrayEscaped));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.get("simple").isJsonArray());
        Assert.assertEquals(arraySimple, JsonArrayMapper.read(output.get("simple").getAsJsonArray()));
        Assert.assertTrue(output.get("escaped").isJsonArray());
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(JsonArrayMapper.read(output.get("escaped").getAsJsonArray())));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true, true);
        data.setTranslation("chars", create(specialCharacters));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(specialCharacters, output.get("chars").getAsString());

        TranslationData input = new TranslationData(true, true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(specialCharacters, StringEscapeUtils.unescapeJava(input.getTranslation("chars").get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true, true);
        data.setTranslation("nested.key.section", create("test"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertEquals("test", output.getAsJsonObject("nested").getAsJsonObject("key").get("section").getAsString());

        TranslationData input = new TranslationData(true, true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation("nested.key.section").get("en"));
    }

    @Override
    public void testNonNestedKeys() {
        TranslationData data = new TranslationData(true, false);
        data.setTranslation("long.key.with.many.sections", create("test"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertTrue(output.has("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true, false);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals("test", input.getTranslation("long.key.with.many.sections").get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true, true);
        data.setTranslation("space", create(leadingSpace));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(leadingSpace, output.get("space").getAsString());

        TranslationData input = new TranslationData(true, true);
        JsonMapper.read("en", output, input.getRootNode());

        Assert.assertEquals(leadingSpace, input.getTranslation("space").get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true, true);
        data.setTranslation("numbered", create("15000"));

        JsonObject output = new JsonObject();
        JsonMapper.write("en", output, data.getRootNode());

        Assert.assertEquals(15000, output.get("numbered").getAsNumber());

        JsonObject input = new JsonObject();
        input.addProperty("numbered", 143.23);
        JsonMapper.read("en", input, data.getRootNode());

        Assert.assertEquals("143.23", data.getTranslation("numbered").get("en"));
    }
}