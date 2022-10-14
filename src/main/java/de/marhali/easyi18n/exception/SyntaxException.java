package de.marhali.easyi18n.exception;

import de.marhali.easyi18n.model.TranslationFile;

/**
 * Indicates a syntax error in a managed translation file.
 * @author marhali
 */
public class SyntaxException extends RuntimeException {
    private final TranslationFile file;

    public SyntaxException(String message, TranslationFile file) {
        super(message);
        this.file = file;
    }

    public TranslationFile getFile() {
        return file;
    }
}
