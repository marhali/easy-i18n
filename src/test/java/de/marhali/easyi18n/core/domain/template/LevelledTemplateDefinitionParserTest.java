package de.marhali.easyi18n.core.domain.template;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author marhali
 */
public class LevelledTemplateDefinitionParserTest {
    @Test
    public void testParse_WithMultipleLevelsAndDelimiters() {
        String input = "[segment 1]delimA[segment 2]delimB[segment 3]";

        LevelledTemplate result = LevelledTemplateDefinitionParser.parse(input);

        Assert.assertNotNull(result);

        List<Template> levels = result.levels();
        List<String> delimiters = result.delimiters();

        Assert.assertEquals(3, levels.size());
        Assert.assertEquals(2, delimiters.size());

        Assert.assertEquals("delimA", delimiters.get(0));
        Assert.assertEquals("delimB", delimiters.get(1));

        Assert.assertEquals(TemplateDefinitionParser.parse("segment 1"), levels.get(0));
        Assert.assertEquals(TemplateDefinitionParser.parse("segment 2"), levels.get(1));
        Assert.assertEquals(TemplateDefinitionParser.parse("segment 3"), levels.get(2));
    }

    @Test
    public void testParse_WithSingleLevelAndNoDelimiter() {
        String input = "[onlyOne]";

        LevelledTemplate result = LevelledTemplateDefinitionParser.parse(input);

        Assert.assertNotNull(result);

        List<Template> levels = result.levels();
        List<String> delimiters = result.delimiters();

        Assert.assertEquals(1, levels.size());
        Assert.assertEquals(0, delimiters.size());

        Assert.assertEquals(TemplateDefinitionParser.parse("onlyOne"), levels.getFirst());
    }

    @Test
    public void testParse_WithEmptyDelimiterBetweenLevels() {
        String input = "[a][b]";

        LevelledTemplate result = LevelledTemplateDefinitionParser.parse(input);

        Assert.assertNotNull(result);

        List<Template> levels = result.levels();
        List<String> delimiters = result.delimiters();

        Assert.assertEquals(2, levels.size());
        Assert.assertEquals(1, delimiters.size());

        Assert.assertEquals("", delimiters.getFirst());
    }
}
