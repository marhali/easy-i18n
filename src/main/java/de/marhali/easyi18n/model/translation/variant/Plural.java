package de.marhali.easyi18n.model.translation.variant;

/**
 * Represents all possible pluralization forms a translation can support.
 * @author marhali
 */
public enum Plural {
    ZERO, ONE, TWO, FEW, MANY, OTHER;

    public static final Plural DEFAULT = Plural.ONE;
}
