package de.marhali.easyi18n;

import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationNode;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Unit tests for {@link TranslationData} in combination with {@link TranslationNode}
 * @author marhali
 */
public class TranslationDataTest {

    private final int numOfTranslations = 14;
    private final Translation translation = new Translation("en", "test");

    private void addTranslations(TranslationData data) {
        data.setTranslation(KeyPath.of("zulu"), translation);
        data.setTranslation(KeyPath.of("gamma"), translation);

        data.setTranslation(KeyPath.of("foxtrot.super.long.key"), translation);
        data.setTranslation(KeyPath.of("foxtrot", "super", "long", "key"), translation);

        data.setTranslation(KeyPath.of("charlie.b", "sub"), translation);
        data.setTranslation(KeyPath.of("charlie.a", "sub"), translation);

        data.setTranslation(KeyPath.of("bravo.b"), translation);
        data.setTranslation(KeyPath.of("bravo.c"), translation);
        data.setTranslation(KeyPath.of("bravo.a"), translation);
        data.setTranslation(KeyPath.of("bravo.d"), translation);

        data.setTranslation(KeyPath.of("bravo", "b"), translation);
        data.setTranslation(KeyPath.of("bravo", "c"), translation);
        data.setTranslation(KeyPath.of("bravo", "a"), translation);
        data.setTranslation(KeyPath.of("bravo", "d"), translation);
    }

    @Test
    public void testKeySorting() {
        TranslationData data = new TranslationData(true);
        this.addTranslations(data);

        Set<KeyPath> expectation = new LinkedHashSet<>(Arrays.asList(
                KeyPath.of("bravo", "a"), KeyPath.of("bravo", "b"), KeyPath.of("bravo", "c"), KeyPath.of("bravo", "d"),
                KeyPath.of("bravo.a"), KeyPath.of("bravo.b"), KeyPath.of("bravo.c"), KeyPath.of("bravo.d"),
                KeyPath.of("charlie.a", "sub"), KeyPath.of("charlie.b", "sub"),
                KeyPath.of("foxtrot", "super", "long", "key"),
                KeyPath.of("foxtrot.super.long.key"),
                KeyPath.of("gamma"),
                KeyPath.of("zulu")
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations);
    }

    @Test
    public void testKeyUnordered() {
        TranslationData data = new TranslationData(false);
        this.addTranslations(data);

        Set<KeyPath> expectation = new LinkedHashSet<>(Arrays.asList(
                KeyPath.of("zulu"),
                KeyPath.of("gamma"),
                KeyPath.of("foxtrot.super.long.key"),
                KeyPath.of("foxtrot", "super", "long", "key"),
                KeyPath.of("charlie.b", "sub"), KeyPath.of("charlie.a", "sub"),
                KeyPath.of("bravo.b"), KeyPath.of("bravo.c"), KeyPath.of("bravo.a"), KeyPath.of("bravo.d"),
                KeyPath.of("bravo", "b"), KeyPath.of("bravo", "c"), KeyPath.of("bravo", "a"), KeyPath.of("bravo", "d")
        ));

        Assert.assertEquals(data.getFullKeys(), expectation);
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations);
    }

    @Test
    public void testDelete() {
        TranslationData data = new TranslationData(true);

        data.setTranslation(KeyPath.of("alpha"), translation);
        data.setTranslation(KeyPath.of("nested.alpha"), translation);
        data.setTranslation(KeyPath.of("nested.long.bravo"), translation);

        data.setTranslation(KeyPath.of("beta"), translation);
        data.setTranslation(KeyPath.of("nested", "alpha"), translation);
        data.setTranslation(KeyPath.of("nested", "long", "bravo"), translation);

        Assert.assertEquals(data.getFullKeys().size(), 6);

        data.setTranslation(KeyPath.of("alpha"), null);
        data.setTranslation(KeyPath.of("nested.alpha"), null);
        data.setTranslation(KeyPath.of("nested.long.bravo"), null);

        Assert.assertEquals(data.getFullKeys().size(), 3);

        data.setTranslation(KeyPath.of("beta"), null);
        data.setTranslation(KeyPath.of("nested", "alpha"), null);
        data.setTranslation(KeyPath.of("nested", "long", "bravo"), null);

        Assert.assertEquals(data.getFullKeys().size(), 0);

        Assert.assertNull(data.getTranslation(KeyPath.of("alpha")));
        Assert.assertNull(data.getTranslation(KeyPath.of("nested.alpha")));
        Assert.assertNull(data.getTranslation(KeyPath.of("nested.long.bravo")));

        Assert.assertNull(data.getTranslation(KeyPath.of("beta")));
        Assert.assertNull(data.getTranslation(KeyPath.of("nested", "alpha")));
        Assert.assertNull(data.getTranslation(KeyPath.of("nested", "long", "bravo")));
    }

    @Test
    public void testDeleteRecursively() {
        TranslationData data = new TranslationData(true);
        this.addTranslations(data);

        data.setTranslation(KeyPath.of("foxtrot.super.long.key"), null);
        data.setTranslation(KeyPath.of("foxtrot", "super", "long", "key"), null);

        Assert.assertNull(data.getTranslation(KeyPath.of("foxtrot.super.long.key")));
        Assert.assertNull(data.getRootNode().getChildren().get("foxtrot"));
        Assert.assertEquals(data.getFullKeys().size(), numOfTranslations  - 2);
    }

    @Test
    public void testOverwrite() {
        TranslationData data = new TranslationData(true);

        Translation before = new Translation("en", "before");
        Translation after = new Translation("en", "after");

        data.setTranslation(KeyPath.of("alpha"), before);
        data.setTranslation(KeyPath.of("nested.alpha"), before);
        data.setTranslation(KeyPath.of("nested.long.bravo"), before);
        data.setTranslation(KeyPath.of("beta"), before);
        data.setTranslation(KeyPath.of("nested", "alpha"), before);
        data.setTranslation(KeyPath.of("nested", "long", "bravo"), before);

        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha")), before);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested.alpha")), before);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested.long.bravo")), before);
        Assert.assertEquals(data.getTranslation(KeyPath.of("beta")), before);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested", "alpha")), before);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested", "long", "bravo")), before);

        data.setTranslation(KeyPath.of("alpha"), after);
        data.setTranslation(KeyPath.of("nested.alpha"), after);
        data.setTranslation(KeyPath.of("nested.long.bravo"), after);
        data.setTranslation(KeyPath.of("beta"), after);
        data.setTranslation(KeyPath.of("nested", "alpha"), after);
        data.setTranslation(KeyPath.of("nested", "long", "bravo"), after);

        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha")), after);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested.alpha")), after);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested.long.bravo")), after);
        Assert.assertEquals(data.getTranslation(KeyPath.of("beta")), after);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested", "alpha")), after);
        Assert.assertEquals(data.getTranslation(KeyPath.of("nested", "long", "bravo")), after);
    }

    @Test
    public void testTransformRecursively() {
        TranslationData data = new TranslationData(true);

        data.setTranslation(KeyPath.of("alpha.nested.key"), translation);
        data.setTranslation(KeyPath.of("alpha.other"), translation);
        data.setTranslation(KeyPath.of("bravo"), translation);
        data.setTranslation(KeyPath.of("alpha", "nested", "key"), translation);
        data.setTranslation(KeyPath.of("alpha", "other"), translation);
        data.setTranslation(KeyPath.of("charlie"), translation);

        Assert.assertEquals(6, data.getFullKeys().size());

        data.setTranslation(KeyPath.of("alpha.nested"), translation);
        data.setTranslation(KeyPath.of("alpha.other.new"), translation);
        data.setTranslation(KeyPath.of("bravo"), null);
        data.setTranslation(KeyPath.of("alpha", "nested"), translation);
        data.setTranslation(KeyPath.of("alpha", "other", "new"), translation);
        data.setTranslation(KeyPath.of("charlie"), null);

        Assert.assertEquals(6, data.getFullKeys().size());

        Assert.assertNotNull(data.getTranslation(KeyPath.of("alpha.nested.key")));
        Assert.assertNotNull(data.getTranslation(KeyPath.of("alpha.other")));
        Assert.assertNull(data.getTranslation(KeyPath.of("bravo")));
        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha.nested")), translation);
        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha.other.new")), translation);

        Assert.assertNull(data.getTranslation(KeyPath.of("alpha", "nested", "key")));
        Assert.assertNull(data.getTranslation(KeyPath.of("alpha", "other")));
        Assert.assertNull(data.getTranslation(KeyPath.of("charlie")));
        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha", "nested")), translation);
        Assert.assertEquals(data.getTranslation(KeyPath.of("alpha", "other", "new")), translation);
    }
}