package de.marhali.easyi18n.core.domain.rules;

/**
 * Enumeration of all possible modes to use against a rule constraint.
 *
 * @author marhali
 */
public enum TextMatchMode {
    EXACT,
    PREFIX,
    SUFFIX,
    CONTAINS,
    REGEX
}
