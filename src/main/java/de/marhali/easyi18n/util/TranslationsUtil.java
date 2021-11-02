package de.marhali.easyi18n.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utility tool to support the translations instance
 * @author marhali
 */
@Deprecated // SectionUtil
public class TranslationsUtil {

    /**
     * Retrieve all sections for the specified path (mostly fullPath)
     * @param path The path
     * @return Sections. E.g. input user.username.title -> Output: [user, username, title]
     */
    public static @NotNull List<String> getSections(@NotNull String path) {
        if(!path.contains(".")) {
            return new ArrayList<>(Collections.singletonList(path));
        }

        return new ArrayList<>(Arrays.asList(path.split("\\.")));
    }

    /**
     * Concatenate the given sections to a single string.
     * @param sections The sections
     * @return Full path. E.g. input [user, username, title] -> Output: user.username.title
     */
    public static @NotNull String sectionsToFullPath(@NotNull List<String> sections) {
        StringBuilder builder = new StringBuilder();

        for (String section : sections) {
            if(builder.length() > 0) {
                builder.append(".");
            }

            builder.append(section);
        }

        return builder.toString();
    }
}