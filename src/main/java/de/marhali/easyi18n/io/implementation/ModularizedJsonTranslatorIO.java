package de.marhali.easyi18n.io.implementation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.TranslatorIO;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * IO operations for splitted / modularized json files. Each locale can have multiple translation files.
 * @author marhali
 */
public class ModularizedJsonTranslatorIO implements TranslatorIO {

    private static final String FILE_EXTENSION = "json";

    @Override
    public void read(@NotNull Project project, @NotNull String directoryPath, @NotNull Consumer<Translations> callback) {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

            if(directory == null || directory.getChildren() == null) {
                throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
            }

            VirtualFile[] localeDirectories = directory.getChildren();

            List<String> locales = new ArrayList<>();
            LocalizedNode nodes = new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>());

            try {
                for(VirtualFile localeDir : localeDirectories) {
                    String locale = localeDir.getName();
                    locales.add(locale);

                    // Read all json modules
                    for(VirtualFile module : localeDir.getChildren()) {

                        if(!IOUtil.isFileRelevant(project, module)) { // File does not matches pattern
                            continue;
                        }

                        JsonObject tree = JsonParser.parseReader(new InputStreamReader(module.getInputStream(),
                                module.getCharset())).getAsJsonObject();

                        String moduleName = module.getNameWithoutExtension();
                        LocalizedNode moduleNode = nodes.getChildren(moduleName);

                        if(moduleNode == null) { // Create module / sub node
                            moduleNode = new LocalizedNode(moduleName, new ArrayList<>());
                            nodes.addChildren(moduleNode);
                        }

                        JsonUtil.readTree(locale, tree, moduleNode);
                    }
                }

                callback.accept(new Translations(locales, nodes));

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

    @Override
    public void save(@NotNull Project project, @NotNull Translations translations,
                     @NotNull String directoryPath, @NotNull Consumer<Boolean> callback) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : translations.getLocales()) {
                    // Use top level children as modules
                    for (LocalizedNode module : translations.getNodes().getChildren()) {
                        JsonObject content = new JsonObject();
                        JsonUtil.writeTree(locale, content, module);

                        String fullPath = directoryPath + "/" + locale + "/" + module.getKey() + "." + FILE_EXTENSION;
                        File file = new File(fullPath);
                        boolean created = file.createNewFile();

                        VirtualFile vf = created ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                                : LocalFileSystem.getInstance().findFileByIoFile(file);

                        vf.setBinaryContent(gson.toJson(content).getBytes(vf.getCharset()));
                    }
                }

                // Successfully saved
                callback.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }
}