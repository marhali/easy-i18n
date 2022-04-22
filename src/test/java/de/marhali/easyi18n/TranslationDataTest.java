package de.marhali.easyi18n;

import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;

import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for {@link TranslationData} in combination with {@link TranslationNode}
 * @author marhali
 */
public class TranslationDataTest {

    private final int numOfTranslations = 14;
    private final TranslationValue translation = new TranslationValue("en", "test");

    private void addTranslations(TranslationData data) {
        data.setTranslation(new KeyPath("zulu"), translation);
        data.setTranslation(new KeyPath("gamma"), translation);

        data.setTranslation(new KeyPath("foxtrot.super.long.key"), translation);
        data.setTranslation(new KeyPath("foxtrot", "super", "long", "key"), translation);

        data.setTranslation(new KeyPath("charlie.b", "sub"), translation);
        data.setTranslation(new KeyPath("charlie.a", "sub"), translation);

        data.setTranslation(new KeyPath("bravo.b"), translation);
        data.setTranslation(new KeyPath("bravo.c"), translation);
        data.setTranslation(new KeyPath("bravo.a"), translation);
        data.setTranslation(new KeyPath("bravo.d"), translation);

        data.setTranslation(new KeyPath("bravo", "b"), translation);
        data.setTranslation(new KeyPath("bravo", "c"), translation);
        data.setTranslation(new KeyPath("bravo", "a"), translation);
        data.setTranslation(new KeyPath("bravo", "d"), translation);
    }

    @Test
    public void testKeySorting() {
        TranslationData data = new TranslationData(true);
        this.addTranslations(data);

        Set<KeyPath> expectation = new LinkedHashSet<>(Arrays.asList(
                new KeyPath("bravo", "a"), new KeyPath("bravo", "b"), new KeyPath("bravo", "c"), new KeyPath("bravo", "d"),
                new KeyPath("bravo.a"), new KeyPath("bravo.b"), new KeyPath("bravo.c"), new KeyPath("bravo.d"),
                new KeyPath("charlie.a", "sub"), new KeyPath("charlie.b", "sub"),
                new KeyPath("foxtrot", "super", "long", "key"),
                new KeyPath("foxtrot.super.long.key"),
                new KeyPath("gamma"),
                new KeyPath("zulu")
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations);
    }

    @Test
    public void testKeyUnordered() {
        TranslationData data = new TranslationData(false);
        this.addTranslations(data);

        Set<KeyPath> expectation = new LinkedHashSet<>(Arrays.asList(
                new KeyPath("zulu"),
                new KeyPath("gamma"),
                new KeyPath("foxtrot.super.long.key"),
                new KeyPath("foxtrot", "super", "long", "key"),
                new KeyPath("charlie.b", "sub"), new KeyPath("charlie.a", "sub"),
                new KeyPath("bravo.b"), new KeyPath("bravo.c"), new KeyPath("bravo.a"), new KeyPath("bravo.d"),
                new KeyPath("bravo", "b"), new KeyPath("bravo", "c"), new KeyPath("bravo", "a"), new KeyPath("bravo", "d")
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations);
    }

    @Test
    public void testDelete() {
        TranslationData data = new TranslationData(true);

        data.setTranslation(new KeyPath("alpha"), translation);
        data.setTranslation(new KeyPath("nested.alpha"), translation);
        data.setTranslation(new KeyPath("nested.long.bravo"), translation);

        data.setTranslation(new KeyPath("beta"), translation);
        data.setTranslation(new KeyPath("nested", "alpha"), translation);
        data.setTranslation(new KeyPath("nested", "long", "bravo"), translation);

        Assert.assertEquals(data.getFullKeys().size(), 6);

        data.setTranslation(new KeyPath("alpha"), null);
        data.setTranslation(new KeyPath("nested.alpha"), null);
        data.setTranslation(new KeyPath("nested.long.bravo"), null);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation(new KeyPath("beta"), null);
        data.setTranslation(new KeyPath("nested", "alpha"), null);
        data.setTranslation(new KeyPath("nested", "long", "bravo"), null);

        Assert.assertEquals(data.getFullKeys().size(), 0);

        Assert.assertNull(data.getTranslation(new KeyPath("alpha")));
        Assert.assertNull(data.getTranslation(new KeyPath("nested.alpha")));
        Assert.assertNull(data.getTranslation(new KeyPath("nested.long.bravo")));

        Assert.assertNull(data.getTranslation(new KeyPath("beta")));
        Assert.assertNull(data.getTranslation(new KeyPath("nested", "alpha")));
        Assert.assertNull(data.getTranslation(new KeyPath("nested", "long", "bravo")));
    }

    @Test
    public void testDeleteRecursively() {
        TranslationData data = new TranslationData(true);
        this.addTranslations(data);

        data.setTranslation(new KeyPath("foxtrot.super.long.key"), null);
        data.setTranslation(new KeyPath("foxtrot", "super", "long", "key"), null);

        Assert.assertNull(data.getTranslation(new KeyPath("foxtrot.super.long.key")));
        Assert.assertNull(data.getRootNode().getChildren().get("foxtrot"));
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations  - 2);
    }

    @Test
    public void testOverwrite() {
        TranslationData data = new TranslationData(true);

        TranslationValue before = new TranslationValue("en", "before");
        TranslationValue after = new TranslationValue("en", "after");

        data.setTranslation(new KeyPath("alpha"), before);
        data.setTranslation(new KeyPath("nested.alpha"), before);
        data.setTranslation(new KeyPath("nested.long.bravo"), before);
        data.setTranslation(new KeyPath("beta"), before);
        data.setTranslation(new KeyPath("nested", "alpha"), before);
        data.setTranslation(new KeyPath("nested", "long", "bravo"), before);

        Assert.assertEquals(data.getTranslation(new KeyPath("alpha")), before);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested.alpha")), before);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested.long.bravo")), before);
        Assert.assertEquals(data.getTranslation(new KeyPath("beta")), before);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested", "alpha")), before);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested", "long", "bravo")), before);

        data.setTranslation(new KeyPath("alpha"), after);
        data.setTranslation(new KeyPath("nested.alpha"), after);
        data.setTranslation(new KeyPath("nested.long.bravo"), after);
        data.setTranslation(new KeyPath("beta"), after);
        data.setTranslation(new KeyPath("nested", "alpha"), after);
        data.setTranslation(new KeyPath("nested", "long", "bravo"), after);

        Assert.assertEquals(data.getTranslation(new KeyPath("alpha")), after);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested.alpha")), after);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested.long.bravo")), after);
        Assert.assertEquals(data.getTranslation(new KeyPath("beta")), after);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested", "alpha")), after);
        Assert.assertEquals(data.getTranslation(new KeyPath("nested", "long", "bravo")), after);
    }

    @Test
    public void testTransformRecursively() {
        TranslationData data = new TranslationData(true);

        data.setTranslation(new KeyPath("alpha.nested.key"), translation);
        data.setTranslation(new KeyPath("alpha.other"), translation);
        data.setTranslation(new KeyPath("bravo"), translation);
        data.setTranslation(new KeyPath("alpha", "nested", "key"), translation);
        data.setTranslation(new KeyPath("alpha", "other"), translation);
        data.setTranslation(new KeyPath("charlie"), translation);

        Assert.assertEquals(6, data.getFullKeys().size());

        data.setTranslation(new KeyPath("alpha.nested"), translation);
        data.setTranslation(new KeyPath("alpha.other.new"), translation);
        data.setTranslation(new KeyPath("bravo"), null);
        data.setTranslation(new KeyPath("alpha", "nested"), translation);
        data.setTranslation(new KeyPath("alpha", "other", "new"), translation);
        data.setTranslation(new KeyPath("charlie"), null);

        Assert.assertEquals(6, data.getFullKeys().size());

        Assert.assertNotNull(data.getTranslation(new KeyPath("alpha.nested.key")));
        Assert.assertNotNull(data.getTranslation(new KeyPath("alpha.other")));
        Assert.assertNull(data.getTranslation(new KeyPath("bravo")));
        Assert.assertEquals(data.getTranslation(new KeyPath("alpha.nested")), translation);
        Assert.assertEquals(data.getTranslation(new KeyPath("alpha.other.new")), translation);

        Assert.assertNull(data.getTranslation(new KeyPath("alpha", "nested", "key")));
        Assert.assertNull(data.getTranslation(new KeyPath("alpha", "other")));
        Assert.assertNull(data.getTranslation(new KeyPath("charlie")));
        Assert.assertEquals(data.getTranslation(new KeyPath("alpha", "nested")), translation);
        Assert.assertEquals(data.getTranslation(new KeyPath("alpha", "other", "new")), translation);
    }
}