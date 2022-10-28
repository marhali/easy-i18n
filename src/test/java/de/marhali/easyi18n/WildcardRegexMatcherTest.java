package de.marhali.easyi18n;

import de.marhali.easyi18n.util.WildcardRegexMatcher;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link WildcardRegexMatcher}.
 * @author marhali
 */
public class WildcardRegexMatcherTest extends WildcardRegexMatcher {
    @Test
    public void testWildcard() {
        Assert.assertTrue(matchWildcardRegex("en.json", "*.json"));
        Assert.assertTrue(matchWildcardRegex("de.json", "*.json"));
        Assert.assertFalse(matchWildcardRegex("index.html", "*.json"));

        Assert.assertTrue(matchWildcardRegex("en.json", "*.*"));
        Assert.assertFalse(matchWildcardRegex("file", "*.*"));

        Assert.assertTrue(matchWildcardRegex("en.txt", "*.???"));
        Assert.assertFalse(matchWildcardRegex("en.json", "*.???"));
    }

    @Test
    public void testRegex() {
        Assert.assertTrue(matchWildcardRegex("en.json", "^(en|de)\\.json"));
        Assert.assertFalse(matchWildcardRegex("gb.json", "^(en|de)\\.json"));

        Assert.assertTrue(matchWildcardRegex("en.jpg", "^.*\\.(jpg|JPG|gif|GIF)$"));
        Assert.assertFalse(matchWildcardRegex("en.json", "^.*\\.(jpg|JPG|gif|GIF)$"));
    }
}