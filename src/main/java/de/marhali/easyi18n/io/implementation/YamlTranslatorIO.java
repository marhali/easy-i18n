package de.marhali.easyi18n.io.implementation;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;

import de.marhali.easyi18n.io.*;
import de.marhali.easyi18n.model.*;
import de.marhali.easyi18n.util.*;
import de.marhali.easyi18n.util.array.YamlArrayUtil;

import org.jetbrains.annotations.*;

import thito.nodeflow.config.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

public class YamlTranslatorIO implements TranslatorIO {
    @Override
    public void read(@NotNull Project project, @NotNull String directoryPath, @NotNull Consumer<Translations> callback) {
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

                    if(!IOUtil.isFileRelevant(project, file)) { // File does not matches pattern
                        continue;
                    }

                    locales.add(file.getNameWithoutExtension());

                    try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
                        Section section = Section.parseToMap(reader);
                        load(file.getNameWithoutExtension(), nodes, section);
                    }
                }

                callback.accept(new Translations(locales, nodes));

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

    private void load(String locale, LocalizedNode node, Section section) {
        if (section instanceof MapSection) {
            for (String key : section.getKeys()) {
                LocalizedNode child = node.getChildren(key);
                if (child == null) {
                    node.addChildren(child = new LocalizedNode(key, new ArrayList<>()));
                }
                LocalizedNode finalChild = child;
                MapSection map = section.getMap(key).orElse(null);
                if (map != null) {
                    load(locale, finalChild, map);
                } else {

                    if(section.isList(key) && section.getList(key).isPresent()) {
                        child.getValue().put(locale, YamlArrayUtil.read(section.getList(key).get()));
                    } else {
                        String value = section.getString(key).orElse(null);
                        if (value != null) {
                            child.getValue().put(locale, value);
                        }
                    }
                }
            }
        }
    }

    private void save(LocalizedNode node, String locale, Section section, String path) {
        if (node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            String value = node.getValue().get(locale);
            if (value != null) {
                section.set(path, YamlArrayUtil.isArray(value) ? YamlArrayUtil.write(value) : value);
            }
        } else {
            for (LocalizedNode child : node.getChildren()) {
                save(child, locale, section, path == null ? child.getKey() : path + "." + child.getKey());
            }
        }
    }

    @Override
    public void save(@NotNull Project project, @NotNull Translations translations, @NotNull String directoryPath, @NotNull Consumer<Boolean> callback) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : translations.getLocales()) {
                    Section section = new MapSection();

                    save(translations.getNodes(), locale, section, null);

                    String fullPath = directoryPath + "/" + locale + ".yml";
                    VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(fullPath));

                    file.setBinaryContent(Section.toString(section).getBytes(file.getCharset()));
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
