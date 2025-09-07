package de.marhali.easyi18n.mapper;

import de.marhali.easyi18n.io.folder.FolderStrategyType;
import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.io.parser.properties.PropertiesArrayMapper;
import de.marhali.easyi18n.io.parser.properties.PropertiesMapper;
import de.marhali.easyi18n.io.parser.properties.SortableProperties;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.settings.presets.NamingConvention;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;

import java.util.*;

/**
 * Unit tests for {@link PropertiesMapper}.
 *
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
        PropertiesMapper.read("en", input, data, converter(true));

        SortableProperties output = new SortableProperties(false);
        PropertiesMapper.write("en", output, data, converter(true));

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
        PropertiesMapper.read("en", input, data, converter(true));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter(true));

        List<String> expect = Arrays.asList("alpha", "bravo", "zulu");
        Assert.assertEquals(expect, new ArrayList<>(output.keySet()));
    }

    @Override
    public void testArrays() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("simple"), create(arraySimple));
        data.setTranslation(new KeyPath("escaped"), create(arrayEscaped));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter(true));

        Assert.assertTrue(output.get("simple") instanceof String[]);
        Assert.assertEquals(arraySimple, PropertiesArrayMapper.read((String[]) output.get("simple")));
        Assert.assertTrue(output.get("escaped") instanceof String[]);
        Assert.assertEquals(arrayEscaped, StringEscapeUtils.unescapeJava(PropertiesArrayMapper.read((String[]) output.get("escaped"))));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input, converter(true));

        Assert.assertTrue(PropertiesArrayMapper.isArray(input.getTranslation(new KeyPath("simple")).get("en")));
        Assert.assertTrue(PropertiesArrayMapper.isArray(input.getTranslation(new KeyPath("escaped")).get("en")));
    }

    @Override
    public void testSpecialCharacters() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("chars"), create(specialCharacters));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter(true));

        Assert.assertEquals(specialCharacters, output.get("chars"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input, converter(true));

        Assert.assertEquals(specialCharacters, StringEscapeUtils.unescapeJava(input.getTranslation(new KeyPath("chars")).get("en")));
    }

    @Override
    public void testNestedKeys() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("nested", "key", "sections"), create("test"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter(true));

        Assert.assertEquals("test", output.get("nested:key.sections"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input, converter(true));

        Assert.assertTrue(input.getRootNode().getChildren().containsKey("nested"));
        Assert.assertEquals("test", input.getTranslation(new KeyPath("nested", "key", "sections")).get("en"));
    }

    @Override
    public void testNonNestedKeys() { // Note: Key nesting is not supported in properties file.
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("long.key.with.many.sections"), create("test"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter(false));

        Assert.assertNotNull(output.get("long.key.with.many.sections"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input, converter(false));

        Assert.assertEquals("test", input.getTranslation(new KeyPath("long.key.with.many.sections")).get("en"));
    }

    @Override
    public void testLeadingSpace() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("space"), create(leadingSpace));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter());

        Assert.assertEquals(leadingSpace, output.get("space"));

        TranslationData input = new TranslationData(true);
        PropertiesMapper.read("en", output, input, converter());

        Assert.assertEquals(leadingSpace, input.getTranslation(new KeyPath("space")).get("en"));
    }

    @Override
    public void testNumbers() {
        TranslationData data = new TranslationData(true);
        data.setTranslation(new KeyPath("numbered"), create("15000"));

        SortableProperties output = new SortableProperties(true);
        PropertiesMapper.write("en", output, data, converter());

        Assert.assertEquals(15000, output.get("numbered"));

        SortableProperties input = new SortableProperties(true);
        input.put("numbered", 143.23);
        PropertiesMapper.read("en", input, data, converter());

        Assert.assertEquals("143.23", data.getTranslation(new KeyPath("numbered")).get("en"));
    }

    private KeyPathConverter converter() {
        return converter(true);
    }

    private KeyPathConverter converter(boolean nestKeys) {
        return new KeyPathConverter(new ProjectSettings() {
            @Override
            public @Nullable String getLocalesDirectory() {
                return null;
            }

            @Override
            public @NotNull FolderStrategyType getFolderStrategy() {
                return FolderStrategyType.MODULARIZED_NAMESPACE;
            }

            @Override
            public @NotNull ParserStrategyType getParserStrategy() {
                return ParserStrategyType.PROPERTIES;
            }

            @Override
            public @NotNull String getFilePattern() {
                return null;
            }

            @Override
            public boolean isSorting() {
                return true;
            }

            @Override
            public @Nullable String getNamespaceDelimiter() {
                return ":";
            }

            @Override
            public @NotNull String getSectionDelimiter() {
                return ".";
            }

            @Override
            public @Nullable String getContextDelimiter() {
                return null;
            }

            @Override
            public @Nullable String getPluralDelimiter() {
                return null;
            }

            @Override
            public @Nullable String getDefaultNamespace() {
                return null;
            }

            @Override
            public @NotNull String getPreviewLocale() {
                return null;
            }

            @Override
            public boolean isNestedKeys() {
                return nestKeys;
            }

            @Override
            public boolean isAssistance() {
                return false;
            }

            @Override
            public boolean isAlwaysFold() {
                return false;
            }

            @Override
            public boolean isAddBlankLine() { return false; }

            @Override
            public String getFlavorTemplate() {
                return "";
            }

            @Override
            public boolean isIncludeSubDirs() {
                return false;
            }

            @Override
            public @NotNull NamingConvention getCaseFormat() {
                return NamingConvention.CAMEL_CASE;
            }
        });
    }
}