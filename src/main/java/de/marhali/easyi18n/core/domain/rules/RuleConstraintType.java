package de.marhali.easyi18n.core.domain.rules;

/**
 * Enumeration of all possible editor assistance rule constraint types.
 *
 * @author marhali
 */
public enum RuleConstraintType {
    LANGUAGE,
    LITERAL_KIND,
    STATIC_ONLY,
    CALLABLE_NAME,
    CALLABLE_FQN,
    RECEIVER_TYPE_FQN,
    ARGUMENT_INDEX,
    ARGUMENT_NAME,
    DECLARATION_NAME,
    DECLARATION_MARKER,
    PROPERTY_NAME,
    PROPERTY_PATH,
    IMPORT_SOURCE,
    FILE_PATH,
    IN_TEST_SOURCES,
    TEXT_PATTERN,
    EXCLUDE
}
