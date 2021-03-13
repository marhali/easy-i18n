package de.marhali.easyi18n.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TranslationsUtil {

    public static @NotNull List<String> getSections(@NotNull String path) {
        if(!path.contains(".")) {
            return new ArrayList<>(Collections.singletonList(path));
        }

        return new ArrayList<>(Arrays.asList(path.split("\\.")));
    }

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