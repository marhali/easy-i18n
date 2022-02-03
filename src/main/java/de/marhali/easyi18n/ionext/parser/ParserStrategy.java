package de.marhali.easyi18n.ionext.parser;

import de.marhali.easyi18n.model.*;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents a parser for a specific file format.
 * @author marhali
 */
public abstract class ParserStrategy {

    protected final @NotNull SettingsState settings;

    public ParserStrategy(@NotNull SettingsState settings) {
        this.settings = settings;
    }

    /**
     * Reads the translation file into the translation data object (consider namespace and locale)
     * @param file File to read from
     * @param data Target translation data to save the parsed data
     */
    public abstract void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws IOException;

    /**
     * Writes the relevant data to the translation file (consider namespace and locale)
     * @param data Translation data cache
     * @param file Target translation file
     */
    public abstract void write(@NotNull TranslationData data, @NotNull TranslationFile file) throws IOException;

    /**
     * Determines translation node to use for parsing
     * @param file Translation file to parse
     * @param data Translations
     * @return TranslationNode to use
     */
    protected @NotNull TranslationNode getOrCreateTargetNode(
            @NotNull TranslationFile file, @NotNull TranslationData data) {

        TranslationNode targetNode = data.getRootNode();

        if(file.getNamespace() != null) {
            String moduleName = file.getNamespace();
            TranslationNode moduleNode = data.getNode(KeyPath.of(moduleName));

            if(moduleNode == null) {
                moduleNode = new TranslationNode(this.settings.isSortKeys());
                data.getRootNode().setChildren(moduleName, moduleNode);
            }

            targetNode = moduleNode;
        }

        return targetNode;
    }

    /**
     * Determines translation node to use for writing
     * @param data Translations
     * @param file Translation file to update
     * @return TranslationNode to use
     */
    protected @NotNull TranslationNode getTargetNode(@NotNull TranslationData data, @NotNull TranslationFile file) {
        TranslationNode targetNode = data.getRootNode();

        if(file.getNamespace() != null) {
            targetNode = data.getNode(KeyPath.of(file.getNamespace()));
        }

        return Objects.requireNonNull(targetNode);
    }
}
