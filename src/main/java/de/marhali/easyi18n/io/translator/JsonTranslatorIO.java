package de.marhali.easyi18n.io.translator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.data.LocalizedNode;
import de.marhali.easyi18n.data.Translations;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Implementation for JSON translation files.
 * @author marhali
 */
public class JsonTranslatorIO implements TranslatorIO {

    @Override
    public Translations read(String directoryPath) throws IOException {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

        if(directory == null || directory.getChildren() == null) {
            throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
        }

        VirtualFile[] files = directory.getChildren();

        List<String> locales = new ArrayList<>();
        LocalizedNode nodes = new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>());

        for(VirtualFile file : files) {
            locales.add(file.getNameWithoutExtension());
            JsonObject tree = JsonParser.parseReader(new InputStreamReader(file.getInputStream())).getAsJsonObject();
            readTree(file.getNameWithoutExtension(), tree, nodes);
        }

        return new Translations(locales, nodes);
    }

    @Override
    public void save(Translations translations) {
        System.out.println("TODO: save");
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
