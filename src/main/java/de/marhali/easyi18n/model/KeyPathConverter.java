package de.marhali.easyi18n.model;

import com.intellij.openapi.project.Project;

import de.marhali.easyi18n.service.SettingsService;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Responsible for mapping {@link KeyPath} into single string and backwards.
 * If nesting is enabled the delimiter within a section is escaped otherwise the delimiter between the key sections.
 * @author marhali
 */
public class KeyPathConverter {

    private final boolean nestKeys;

    public KeyPathConverter(boolean nestKeys) {
        this.nestKeys = nestKeys;
    }

    public KeyPathConverter(@NotNull Project project) {
        this(SettingsService.getInstance(project).getState().isNestedKeys());
    }

    public @NotNull String concat(@NotNull KeyPath path) {
        StringBuilder builder = new StringBuilder();

        for(String section : path) {
            if(builder.length() > 0) {
                if(!this.nestKeys) {
                    builder.append("\\\\");
                }

                builder.append(KeyPath.DELIMITER);
            }

            if(this.nestKeys) {
                builder.append(section.replace(KeyPath.DELIMITER, "\\\\" + KeyPath.DELIMITER));
            } else {
                builder.append(section);
            }
        }

        return builder.toString();
    }

    public @NotNull KeyPath split(@NotNull String concatPath) {
        String[] sections = concatPath.split(this.nestKeys ?
                "(?<!\\\\)" + Pattern.quote(KeyPath.DELIMITER) : Pattern.quote("\\\\" + KeyPath.DELIMITER));

        KeyPath path = new KeyPath();

        for(String section : sections) {
            path.add(section.replace("\\\\" + KeyPath.DELIMITER, KeyPath.DELIMITER));
        }

        return path;
    }

    @Override
    public String toString() {
        return "KeyPathConverter{" +
                "nestKeys=" + nestKeys +
                '}';
    }
}