package de.marhali.easyi18n;

import de.marhali.easyi18n.model.translation.variant.Plural;
import de.marhali.easyi18n.model.translation.variant.ContextMap;
import de.marhali.easyi18n.model.translation.variant.LocaleMap;
import de.marhali.easyi18n.model.translation.Translation;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Unit tests for {@link Translation}.
 * @author marhali
 */
public class TranslationTest {

    @Test
    public void add() {
        Translation value = new Translation();
        value.set(ContextMap.DEFAULT, Plural.DEFAULT, "en", "hello");
        Assert.assertEquals(value.getValue(ContextMap.DEFAULT, Plural.DEFAULT, "en"), "hello");
    }

    @Test
    public void override() {
        Translation value = new Translation();
        value.set(ContextMap.DEFAULT, Plural.DEFAULT, new LocaleMap(Map.of("en", "hello", "de", "hallo")));
        value.set(ContextMap.DEFAULT, Plural.DEFAULT, "en", "new hello");
        Assert.assertEquals(value.getValue(ContextMap.DEFAULT, Plural.DEFAULT, "en"), "new hello");
        Assert.assertEquals(value.getValue(ContextMap.DEFAULT, Plural.DEFAULT, "de"), "hallo");
    }

    @Test
    public void plurals() {
        Translation value = new Translation();
        value.set(ContextMap.DEFAULT, Plural.ONE, "en", "boyfriend");
        value.set(ContextMap.DEFAULT, Plural.MANY, "en", "boyfriends");
        Assert.assertEquals(value.getValue(ContextMap.DEFAULT, Plural.ONE, "en"), "boyfriend");
        Assert.assertEquals(value.getValue(ContextMap.DEFAULT, Plural.MANY, "en"), "boyfriends");
    }
}
