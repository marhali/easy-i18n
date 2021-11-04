package de.marhali.easyi18n.io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.io.IOStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;

import de.marhali.easyi18n.model.TranslationNode;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Strategy for simple json locale files. Each locale has its own file.
 * For example localesPath/en.json, localesPath/de.json.
 * @author marhali
 */
public class JsonIOStrategy implements IOStrategy {

    private static final String FILE_EXTENSION = "json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public boolean canUse(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(directory == null || directory.getChildren() == null) {
            return false;
        }

        for(VirtualFile children : directory.getChildren()) {
            if(!children.isDirectory() && isFileRelevant(state, children)) {
                if(children.getFileType().getDefaultExtension().toLowerCase().equals(FILE_EXTENSION)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void read(@NotNull Project project, @NotNull String localesPath,
                     @NotNull SettingsState state, @NotNull Consumer<@Nullable TranslationData> result) {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

            if(directory == null || directory.getChildren() == null) {
                throw new IllegalArgumentException("Specified folder is invalid (" + localesPath + ")");
            }

            TranslationData data = new TranslationData(state.isSortKeys(), state.isNestedKeys());

            try {
                for(VirtualFile file : directory.getChildren()) {
                    if(!isFileRelevant(state, file)) {
                        continue;
                    }

                    data.addLocale(file.getNameWithoutExtension());

                    JSONObject tree = GSON.fromJson(new InputStreamReader(file.getInputStream(), file.getCharset()), JSONObject.class);
                }
            } catch(IOException e) {
                e.printStackTrace();
                result.accept(null);
            }
        });
    }

    @Override
    public void write(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state, @NotNull TranslationData data, @NotNull Consumer<Boolean> result) {

    }
}
