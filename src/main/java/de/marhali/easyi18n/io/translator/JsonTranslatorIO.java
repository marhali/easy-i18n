package de.marhali.easyi18n.io.translator;

import com.google.gson.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.data.LocalizedNode;
import de.marhali.easyi18n.data.Translations;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;

/**
 * Implementation for JSON translation files.
 * @author marhali
 */
public class JsonTranslatorIO implements TranslatorIO {

    private static final String FILE_EXTENSION = "json";

    @Override
    public void read(@NotNull String directoryPath, @NotNull Consumer<Translations> callback) {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

            if(directory == null || directory.getChildren() == null) {
                throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
            }

            VirtualFile[] files = directory.getChildren();

            List<String> locales = new ArrayList<>();
            LocalizedNode nodes = new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>());

            try {
                for(VirtualFile file : files) {
                    locales.add(file.getNameWithoutExtension());
                    JsonObject tree = JsonParser.parseReader(new InputStreamReader(file.getInputStream())).getAsJsonObject();
                    readTree(file.getNameWithoutExtension(), tree, nodes);
                }

                callback.accept(new Translations(locales, nodes));

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

    @Override
    public void save(@NotNull Translations translations, @NotNull String directoryPath, @NotNull Consumer<Boolean> callback) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : translations.getLocales()) {
                    JsonObject content = new JsonObject();
                    writeTree(locale, content, translations.getNodes());
                    //JsonElement content = writeTree(locale, new JsonObject(), translations.getNodes());

                    String fullPath = directoryPath + "/" + locale + "." + FILE_EXTENSION;
                    VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(fullPath));

                    file.setBinaryContent(gson.toJson(content).getBytes());
                }

                // Successfully saved
                callback.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }

    private void writeTree(String locale, JsonObject parent, LocalizedNode node) {
        if(node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            if(node.getValue().get(locale) != null) {
                parent.add(node.getKey(), new JsonPrimitive(node.getValue().get(locale)));
            }

        } else {
            for(LocalizedNode children : node.getChildren()) {
                if(children.isLeaf()) {
                    writeTree(locale, parent, children);
                } else {
                    JsonObject childrenJson = new JsonObject();
                    writeTree(locale, childrenJson, children);
                    if(childrenJson.size() > 0) {
                        parent.add(children.getKey(), childrenJson);
                    }
                }
            }
        }
    }

    private void readTree(String locale, JsonObject json, LocalizedNode data) {
        for(Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();

            try {
                // Try to go one level deeper
                JsonObject childObject = entry.getValue().getAsJsonObject();

                LocalizedNode childrenNode = new LocalizedNode(key, new ArrayList<>());
                data.addChildren(childrenNode);
                readTree(locale, childObject, childrenNode);

            } catch(IllegalStateException e) { // Reached end for this node
                LocalizedNode leafNode = data.getChildren(key);

                if(leafNode == null) {
                    leafNode = new LocalizedNode(key, new HashMap<>());
                    data.addChildren(leafNode);
                }

                Map<String, String> messages = leafNode.getValue();
                messages.put(locale, entry.getValue().getAsString());
                leafNode.setValue(messages);
            }
        }
    }
}