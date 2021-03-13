package de.marhali.easyi18n.io.translator;

import de.marhali.easyi18n.data.Translations;

import java.io.IOException;

/**
 * Interface to retrieve and save localized messages.
 * Can be implemented by various standards. Such as JSON, Properties-Bundle and so on.
 * @author marhali
 */
public interface TranslatorIO {

    /**
     * Reads localized messages from the persistence layer.
     * @param directoryPath The full path from the parent directory which holds the different locale files.
     * @return Translations model
     * Example entry: username.title => [DE:Benutzername, EN:Username]
     */
    Translations read(String directoryPath) throws IOException;

    /**
     * Writes the provided messages to the persistence layer.
     * @param translations Translatons model to save
     * @see #read(String) More information regards the data map
     */
    void save(Translations translations);
}