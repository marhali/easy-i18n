package de.marhali.easyi18n.io.parser;

import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a parser for a specific file format.
 * @author marhali
 */
public abstract class ParserStrategy {

    protected final @NotNull ProjectSettings settings;

    public ParserStrategy(@NotNull ProjectSettings settings) {
        this.settings = settings;
    }

    /**
     * Reads the translation file into the translation data object (consider namespace and locale)
     * @param file File to read from
     * @param data Target translation data to save the parsed data
     */
    public abstract void read(@NotNull TranslationFile file, @NotNull TranslationData data) throws Exception;

    /**
     * Constructs the relevant data to represents the specified translation file. (consider namespace and locale)
     * @param data Translation data cache
     * @param file Target translation file
     * @return String representing target translation file.
     * Can be null to indicate that the file is not necessary and could be deleted
     */
    public abstract @Nullable String write(@NotNull TranslationData data, @NotNull TranslationFile file) throws Exception;

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
            targetNode = data.getOrCreateNoe(file.getNamespace());
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
            targetNode = data.getNode(new KeyPath(file.getNamespace()));
        }

        return Objects.requireNonNull(targetNode);
    }
}
