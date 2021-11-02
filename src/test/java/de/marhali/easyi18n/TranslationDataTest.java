package de.marhali.easyi18n;

import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.util.TranslationBuilder;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for {@link TranslationData} in combination with {@link TranslationNode}
 * @author marhali
 */
public class TranslationDataTest {

    private final int numOfTranslations = 18;

    private void addTranslations(TranslationData data) throws Exception {
        data.setTranslation("zulu", new TranslationBuilder("en", "test").build());
        data.setTranslation("gamma", new TranslationBuilder("en", "test").build());

        data.setTranslation("foxtrot.super.long.key", new TranslationBuilder("en", "test").build());

        data.setTranslation("bravo.b", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.c", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.a", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.d", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.long.bravo", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.long.charlie.a", new TranslationBuilder("en", "test").build());
        data.setTranslation("bravo.long.alpha", new TranslationBuilder("en", "test").build());

        data.setTranslation("alpha.b", new TranslationBuilder("en", "test").build());
        data.setTranslation("alpha.c", new TranslationBuilder("en", "test").build());
        data.setTranslation("alpha.a", new TranslationBuilder("en", "test").build());
        data.setTranslation("alpha.d", new TranslationBuilder("en", "test").build());

        data.setTranslation("charlie.b", new TranslationBuilder("en", "test").build());
        data.setTranslation("charlie.c", new TranslationBuilder("en", "test").build());
        data.setTranslation("charlie.a", new TranslationBuilder("en", "test").build());
        data.setTranslation("charlie.d", new TranslationBuilder("en", "test").build());
    }

    @Test
    public void testKeySorting() throws Exception {
        TranslationData data = new TranslationData(true, true);
        this.addTranslations(data);

        Set<String> expectation = new LinkedHashSet<>(Arrays.asList(
                "alpha.a", "alpha.b", "alpha.c", "alpha.d",
                "bravo.a", "bravo.b", "bravo.c", "bravo.d",
                "bravo.long.alpha", "bravo.long.bravo", "bravo.long.charlie.a",
                "charlie.a", "charlie.b", "charlie.c", "charlie.d",
                "foxtrot.super.long.key",
                "gamma",
                "zulu"
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
    }

    @Test
    public void testKeyUnordered() throws Exception {
        TranslationData data = new TranslationData(false, true);
        this.addTranslations(data);

        Set<String> expectation = new LinkedHashSet<>(Arrays.asList(
                "zulu",
                "gamma",
                "foxtrot.super.long.key",
                "bravo.b", "bravo.c", "bravo.a", "bravo.d",
                "bravo.long.bravo", "bravo.long.charlie.a", "bravo.long.alpha",
                "alpha.b", "alpha.c", "alpha.a", "alpha.d",
                "charlie.b", "charlie.c", "charlie.a", "charlie.d"
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
    }

    @Test
    public void testKeyNesting() throws Exception {
        TranslationData data = new TranslationData(true, true);

        data.setTranslation("nested.alpha", new TranslationBuilder("en", "test").build());
        data.setTranslation("nested.bravo", new TranslationBuilder("en", "test").build());
        data.setTranslation("other.alpha", new TranslationBuilder("en", "test").build());
        data.setTranslation("other.bravo", new TranslationBuilder("en", "test").build());

        Assert.assertEquals(data.getRootNode().getChildren().size(), 2);

        for(TranslationNode node : data.getRootNode().getChildren().values()) {
            Assert.assertFalse(node.isLeaf());
        }
    }

    @Test
    public void testKeyNonNested() throws Exception {
        TranslationData data = new TranslationData(true, false);
        this.addTranslations(data);

        Assert.assertEquals(data.getRootNode().getChildren().size(), this.numOfTranslations);

        for(TranslationNode node : data.getRootNode().getChildren().values()) {
            Assert.assertTrue(node.isLeaf());
        }
    }

    @Test
    public void testDeleteNested() throws Exception {
        TranslationData data = new TranslationData(true, true);

        Translation value = new TranslationBuilder("en", "test").build();

        data.setTranslation("alpha", value);
        data.setTranslation("nested.alpha", value);
        data.setTranslation("nested.long.bravo", value);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation("alpha", null);
        data.setTranslation("nested.alpha", null);
        data.setTranslation("nested.long.bravo", null);

        Assert.assertEquals(data.getFullKeys().size(), 0);
        Assert.assertNull(data.getTranslation("alpha"));
        Assert.assertNull(data.getTranslation("nested.alpha"));
        Assert.assertNull(data.getTranslation("nested.long.bravo"));
    }

    @Test
    public void testDeleteNonNested() throws Exception {
        TranslationData data = new TranslationData(true, false);

        Translation value = new TranslationBuilder("en", "test").build();

        data.setTranslation("alpha", value);
        data.setTranslation("nested.alpha", value);
        data.setTranslation("nested.long.bravo", value);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation("alpha", null);
        data.setTranslation("nested.alpha", null);
        data.setTranslation("nested.long.bravo", null);

        Assert.assertEquals(data.getFullKeys().size(), 0);
        Assert.assertNull(data.getTranslation("alpha"));
        Assert.assertNull(data.getTranslation("nested.alpha"));
        Assert.assertNull(data.getTranslation("nested.long.bravo"));
    }

    @Test
    public void testRecurseDeleteNonNested() throws Exception {
        TranslationData data = new TranslationData(true, false);
        this.addTranslations(data);

        data.setTranslation("foxtrot.super.long.key", null);

        Assert.assertNull(data.getTranslation("foxtrot.super.long.key"));
        Assert.assertNull(data.getRootNode().getChildren().get("foxtrot"));
    }

    @Test
    public void testRecurseDeleteNested() throws Exception {
        TranslationData data = new TranslationData(true, true);
        this.addTranslations(data);

        data.setTranslation("foxtrot.super.long.key", null);

        Assert.assertNull(data.getTranslation("foxtrot.super.long.key"));
        Assert.assertNull(data.getRootNode().getChildren().get("foxtrot"));
    }

    @Test
    public void testOverwriteNonNested() throws Exception {
        TranslationData data = new TranslationData(true, false);

        Translation before = new TranslationBuilder("en", "before").build();
        Translation after = new TranslationBuilder("en", "after").build();

        data.setTranslation("alpha", before);
        data.setTranslation("nested.alpha", before);
        data.setTranslation("nested.long.bravo", before);

        Assert.assertEquals(data.getTranslation("alpha"), before);
        Assert.assertEquals(data.getTranslation("alpha"), before);
        Assert.assertEquals(data.getTranslation("alpha"), before);

        data.setTranslation("alpha", after);
        data.setTranslation("nested.alpha", after);
        data.setTranslation("nested.long.bravo", after);

        Assert.assertEquals(data.getTranslation("alpha"), after);
        Assert.assertEquals(data.getTranslation("alpha"), after);
        Assert.assertEquals(data.getTranslation("alpha"), after);
    }

    @Test
    public void testOverwriteNested() throws Exception {
        TranslationData data = new TranslationData(true, true);

        Translation before = new TranslationBuilder("en", "before").build();
        Translation after = new TranslationBuilder("en", "after").build();

        data.setTranslation("alpha", before);
        data.setTranslation("nested.alpha", before);
        data.setTranslation("nested.long.bravo", before);

        Assert.assertEquals(data.getTranslation("alpha"), before);
        Assert.assertEquals(data.getTranslation("alpha"), before);
        Assert.assertEquals(data.getTranslation("alpha"), before);

        data.setTranslation("alpha", after);
        data.setTranslation("nested.alpha", after);
        data.setTranslation("nested.long.bravo", after);

        Assert.assertEquals(data.getTranslation("alpha"), after);
        Assert.assertEquals(data.getTranslation("alpha"), after);
        Assert.assertEquals(data.getTranslation("alpha"), after);
    }

    @Test
    public void testRecurseTransformNested() throws Exception {
        TranslationData data = new TranslationData(true, true);

        Translation value = new TranslationBuilder("en", "test").build();

        data.setTranslation("alpha.nested.key", value);
        data.setTranslation("alpha.other", value);
        data.setTranslation("bravo", value);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation("alpha.nested", value);
        data.setTranslation("alpha.other.new", value);
        data.setTranslation("bravo", null);

        Assert.assertEquals(data.getFullKeys().size(), 2);
        Assert.assertNull(data.getTranslation("alpha.nested.key"));
        Assert.assertNull(data.getTranslation("alpha.other"));
        Assert.assertNull(data.getTranslation("bravo"));
        Assert.assertEquals(data.getTranslation("alpha.nested"), value);
        Assert.assertEquals(data.getTranslation("alpha.other.new"), value);
    }

    @Test
    public void testRecurseTransformNonNested() throws Exception {
        TranslationData data = new TranslationData(true, false);

        Translation value = new TranslationBuilder("en", "test").build();

        data.setTranslation("alpha.nested.key", value);
        data.setTranslation("alpha.other", value);
        data.setTranslation("bravo", value);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation("alpha.nested", value);
        data.setTranslation("alpha.other.new", value);
        data.setTranslation("bravo", null);

        Assert.assertEquals(data.getFullKeys().size(), 4);
        Assert.assertNull(data.getTranslation("bravo"));
        Assert.assertEquals(data.getTranslation("alpha.nested.key"), value);
        Assert.assertEquals(data.getTranslation("alpha.other"), value);
        Assert.assertEquals(data.getTranslation("alpha.nested"), value);
        Assert.assertEquals(data.getTranslation("alpha.other.new"), value);
    }
}