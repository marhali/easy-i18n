package de.marhali.easyi18n.util;

import org.apache.commons.io.FilenameUtils;

/**
 * Utilities for wildcard / regex matching.
 * @author marhali
 */
public class WildcardRegexMatcher {
    public static boolean matchWildcardRegex(String string, String pattern) {
        boolean wildcardMatch = FilenameUtils.wildcardMatchOnSystem(string, pattern);

        if(wildcardMatch) {
            return true;
        }

        try {
            return string.matches(pattern);
        } catch (Exception e) {
            return false;
        }
    }
}
