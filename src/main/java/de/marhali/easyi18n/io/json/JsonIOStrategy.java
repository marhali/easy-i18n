package de.marhali.easyi18n.io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.IOStrategy;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.util.NotificationHelper;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Strategy for simple json locale files. Each locale has its own file.
 * For example localesPath/en.json, localesPath/de.json.
 * @author marhali
 */
public class JsonIOStrategy implements IOStrategy {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String FILE_EXTENSION;

    public JsonIOStrategy(@NotNull String fileExtension) {
        this.FILE_EXTENSION = fileExtension;
    }

    @Override
    public boolean canUse(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(directory == null || directory.getChildren() == null) {
            return false;
        }

        for(VirtualFile children : directory.getChildren()) {
            if(!children.isDirectory() && isFileRelevant(state, children)) {
                if(children.getExtension().equalsIgnoreCase(FILE_EXTENSION)) {
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

            for(VirtualFile file : directory.getChildren()) {
                if(file.isDirectory() || !isFileRelevant(state, file)) {
                    continue;
                }

                String locale = file.getNameWithoutExtension();
                data.addLocale(locale);

                try {
                    JsonObject tree = GSON.fromJson(new InputStreamReader(file.getInputStream(), file.getCharset()),
                            JsonObject.class);

                    JsonMapper.read(locale, tree, data.getRootNode());

                } catch (Exception e) {
                    NotificationHelper.createIOError(file.getName(), this.getClass(), e);
                    result.accept(null);
                    return;
                }
            }

            result.accept(data);
        });
    }

    @Override
    public void write(@NotNull Project project, @NotNull String localesPath,
                      @NotNull SettingsState state, @NotNull TranslationData data, @NotNull Consumer<Boolean> result) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : data.getLocales()) {
                    JsonObject content = new JsonObject();
                    JsonMapper.write(locale, content, data.getRootNode());

                    File file = new File(localesPath + "/" + locale + "." + FILE_EXTENSION);
                    boolean exists = file.createNewFile();

                    VirtualFile vf = exists
                            ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                            : LocalFileSystem.getInstance().findFileByIoFile(file);

                    vf.setBinaryContent(GSON.toJson(content).getBytes(vf.getCharset()));
                }

                result.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                result.accept(false);
            }
        });
    }
}