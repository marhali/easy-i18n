package de.marhali.easyi18n;

import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.KeyPathConverter;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link KeyPathConverter}.
 * @author marhali
 */
@Deprecated
public class LegacyKeyPathConverterTest {

    private final KeyPathConverter deepMapper = new KeyPathConverter(true);
    private final KeyPathConverter flatMapper = new KeyPathConverter(false);

    @Test
    public void testNestedConcat() {
        Assert.assertEquals("first\\\\.section.second.third",
                deepMapper.concat(KeyPath.of("first.section", "second", "third")));

        Assert.assertEquals("first.second.third",
                deepMapper.concat(KeyPath.of("first", "second", "third")));
    }

    @Test
    public void testNestedSplit() {
        Assert.assertEquals(KeyPath.of("first.section", "second", "third"),
                deepMapper.split("first\\\\.section.second.third"));

        Assert.assertEquals(KeyPath.of("first", "second", "third"),
                deepMapper.split("first.second.third"));
    }

    @Test
    public void testNonNestedConcat() {
        Assert.assertEquals("flat.map\\\\.deeper",
                flatMapper.concat(KeyPath.of("flat.map", "deeper")));

        Assert.assertEquals("flat.map.keys",
                flatMapper.concat(KeyPath.of("flat.map.keys")));
    }

    @Test
    public void testNonNestedSplit() {
        Assert.assertEquals(KeyPath.of("flat.keys.with", "deep.section"),
                flatMapper.split("flat.keys.with\\\\.deep.section"));

        Assert.assertEquals(KeyPath.of("flat.keys.only"),
                flatMapper.split("flat.keys.only"));
    }
}