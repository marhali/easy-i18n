package de.marhali.easyi18n.util;

import de.marhali.easyi18n.model.translation.KeyPath;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Stateful utility to transform absolute translation keys into their character literal representation and backwards.
 * @author marhali
 */
public class KeyPathConverter {

    private final ProjectSettings settings;

    /**
     * Constructs a new converter instance
     * @param settings Delimiter configuration
     */
    public KeyPathConverter(ProjectSettings settings) {
        this.settings = settings;
    }

    /**
     * Transform to character literal representation
     * @param path Absolute key path
     * @return Character literal
     */
    public @NotNull String toString(@NotNull KeyPath path) {
        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < path.size(); i++) {
            if(i > 0) { // Delimiters
                if(i == 1 && settings.getFolderStrategy().isNamespaceMode() && settings.getNamespaceDelimiter() != null) {
                    builder.append(quoteDelimiter(settings.getNamespaceDelimiter()));
                } else {
                    builder.append(quoteDelimiter(settings.getSectionDelimiter()));
                }
            }

            // Section content
            builder.append(quoteSection(path.get(i)));
        }

        return builder.toString();
    }

    /**
     * Splits provided character literal into key path sections.
     * If namespace mode is activated and none was provided, the default namespace will be added.
     * @return Layered key path sections
     */
    public @NotNull KeyPath fromString(@NotNull String literalPath) {
        KeyPath path = new KeyPath();

        int i = 0;
        for(String section : literalPath.split(getSplitRegex())) {

            // Missing namespace
            if(i == 0 && settings.getFolderStrategy().isNamespaceMode() && hasDefaultNamespace()) {
                if(section.length() == literalPath.length() || !String.valueOf(literalPath.charAt(section.length())).equals(settings.getNamespaceDelimiter())) {
                    path.add(settings.getDefaultNamespace());
                }
            }

            path.add(unquoteSection(section));

            i++;
        }

        return path;
    }

    @Override
    public String toString() {
        return "KeyPathConverter{" +
                "settings=" + settings +
                '}';
    }

    /*
     * INTERNAL METHODS
     */

    private boolean hasDefaultNamespace() {
        return settings.getDefaultNamespace() != null && !settings.getDefaultNamespace().isEmpty();
    }

    private String getSplitRegex() {
       return settings.isNestedKeys()
                ? ("(?<!" + Pattern.quote("\\") + ")" + getSplitCharsRegex())
                : Pattern.quote("\\") + getSplitCharsRegex();
    }

    private String getSplitCharsRegex() {
        StringBuilder builder = new StringBuilder();

        builder.append("(");
        builder.append(Pattern.quote(settings.getSectionDelimiter()));

        // Add optional namespace delimiter if present
        if(hasDefaultNamespace()) {
            builder.append("|");
            builder.append(Pattern.quote(Objects.requireNonNull(settings.getNamespaceDelimiter())));
        }

        builder.append(")");
        return builder.toString();
    }

    /**
     * Securely escape found delimiters inside provided section according to the configured policy.
     */
    private String quoteSection(String section) {
        if(!settings.isNestedKeys()) {
            return section;
        }

        if(hasDefaultNamespace()) {
            section = section.replace(settings.getNamespaceDelimiter(), "\\" + settings.getNamespaceDelimiter());
        }

        section = section.replace(settings.getSectionDelimiter(), "\\" + settings.getSectionDelimiter());
        return section;
    }

    private String unquoteSection(String section) {
        if(hasDefaultNamespace()) {
            section = section.replace("\\" + settings.getNamespaceDelimiter(), settings.getNamespaceDelimiter());
        }

        section = section.replace("\\" + settings.getSectionDelimiter(), settings.getSectionDelimiter());
        return section;
    }

    /**
     * Securely escape provided delimiter according to the configured policy.
     */
    private String quoteDelimiter(String delimiter) {
        return settings.isNestedKeys() ? delimiter :  delimiter.replace(delimiter, "\\" + delimiter);
    }
}