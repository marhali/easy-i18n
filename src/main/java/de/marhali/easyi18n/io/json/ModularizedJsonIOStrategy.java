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
import de.marhali.easyi18n.model.TranslationNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

/**
 * Strategy for distributed json files per locale. Each locale can have multiple modules. The file name
 * of each module will be used as the key for the underlying translations. <br/>
 * Full key example: <moduleFileName>.<username>.<title>
 *
 * @author marhali
 */
public class ModularizedJsonIOStrategy implements IOStrategy {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final String FILE_EXTENSION;

    public ModularizedJsonIOStrategy(@NotNull String fileExtension) {
        this.FILE_EXTENSION = fileExtension;
    }

    @Override
    public boolean canUse(@NotNull Project project, @NotNull String localesPath, @NotNull SettingsState state) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(localesPath));

        if(directory == null || directory.getChildren() == null) {
            return false;
        }

        // We expect something like this:
        // <localesPath>/<localeDir>/<moduleFile>

        for(VirtualFile children : directory.getChildren()) {
            if(children.isDirectory()) { // Contains module folders
                for(VirtualFile moduleFile : children.getChildren()) {
                    if(!moduleFile.isDirectory() && isFileRelevant(state, moduleFile)) {
                        if(moduleFile.getFileType().getDefaultExtension().equalsIgnoreCase(FILE_EXTENSION)) {
                            return true;
                        }
                    }
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
            VirtualFile[] localeDirectories = directory.getChildren();

            try {
                for(VirtualFile localeDir : localeDirectories) {
                    String locale = localeDir.getNameWithoutExtension();
                    data.addLocale(locale);

                    // Read all underlying module files
                    for(VirtualFile module : localeDir.getChildren()) {
                        if(module.isDirectory() || !isFileRelevant(state, module)) {
                            continue;
                        }

                        String moduleName = module.getNameWithoutExtension();

                        TranslationNode moduleNode = data.getNode(moduleName) != null
                                ? data.getNode(moduleName)
                                : new TranslationNode(state.isSortKeys() ? new TreeMap<>() : new LinkedHashMap<>());

                        JsonObject tree = GSON.fromJson(new InputStreamReader(module.getInputStream(),
                                module.getCharset()), JsonObject.class);

                        JsonMapper.read(locale, tree, moduleNode);

                        data.getRootNode().setChildren(moduleName, moduleNode);
                    }
                }

                result.accept(data);

            } catch(IOException e) {
                e.printStackTrace();
                result.accept(null);
            }
        });
    }

    // TODO: there will be problems when adding translations via TranslationData with non-nested key mode

    @Override
    public void write(@NotNull Project project, @NotNull String localesPath,
                      @NotNull SettingsState state, @NotNull TranslationData data, @NotNull Consumer<Boolean> result) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : data.getLocales()) {
                    for(Map.Entry<String, TranslationNode> entry : data.getRootNode().getChildren().entrySet()) {
                        String module = entry.getKey();

                        JsonObject content = new JsonObject();
                        JsonMapper.write(locale, content, entry.getValue());

                        String fullPath = localesPath + "/" + locale + "/" + module + "." + FILE_EXTENSION;
                        File file = new File(fullPath);
                        boolean exists = file.createNewFile();

                        VirtualFile vf = exists
                                ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                                : LocalFileSystem.getInstance().findFileByIoFile(file);

                        vf.setBinaryContent(GSON.toJson(content).getBytes(vf.getCharset()));
                    }
                }

                result.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                result.accept(false);
            }
        });
    }
}
