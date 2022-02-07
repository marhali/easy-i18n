package de.marhali.easyi18n.exception;

/**
 * Indicates that the translation's directory has not been configured yet
 * @author marhali
 */
public class EmptyLocalesDirException extends IllegalArgumentException {
    public EmptyLocalesDirException(String message) {
        super(message);
    }
}
