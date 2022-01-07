package de.marhali.easyi18n.util;

import de.marhali.easyi18n.service.SettingsService;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility tool for split and merge translation key paths.
 * Some i18n implementations require to NOT nest the translation keys.
 * This util takes care of this and checks the configured setting for this case.
 * @author marhali
 */
public class PathUtil {

    public static final String DELIMITER = ".";

    private final boolean nestKeys;

    public PathUtil(boolean nestKeys) {
        this.nestKeys = nestKeys;
    }

    public PathUtil(Project project) {
        this.nestKeys = SettingsService.getInstance(project).getState().isNestedKeys();
    }

    public @NotNull List<String> split(@NotNull String path) {
        // Does not contain any sections or key nesting is disabled
        if(!path.contains(DELIMITER) || !nestKeys) {
            return new ArrayList<>(Collections.singletonList(path));
        }

        return new ArrayList<>(Arrays.asList(
                path.split("(?<!\\\\)" + Pattern.quote(DELIMITER))));
    }

    public @NotNull String concat(@NotNull List<String> sections) {
        StringBuilder builder = new StringBuilder();

        // For disabled key nesting this should be only one section
        for(String section : sections) {
            if(builder.length() > 0) {
                builder.append(DELIMITER);
            }

            builder.append(section);
        }

        return builder.toString();
    }

    public @NotNull String append(@NotNull String parentPath, @NotNull String children) {
        StringBuilder builder = new StringBuilder(parentPath);

        if(builder.length() > 0) { // Only add delimiter between parent and child if parent is NOT empty
            builder.append(DELIMITER);
        }

        return builder.append(children).toString();
    }

    @Override
    public String toString() {
        return "PathUtil{" +
                "nestKeys=" + nestKeys +
                '}';
    }
}