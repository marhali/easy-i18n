package de.marhali.easyi18n.io.implementation;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.TranslatorIO;
import de.marhali.easyi18n.util.SortedProperties;
import de.marhali.easyi18n.util.StringUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Implementation for properties translation files.
 * @author marhali
 */
public class PropertiesTranslatorIO implements TranslatorIO {

    public static final String FILE_EXTENSION = "properties";

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
                for (VirtualFile file : files) {

                    if(!IOUtil.isFileRelevant(project, file)) { // File does not matches pattern
                        continue;
                    }

                    locales.add(file.getNameWithoutExtension());
                    SortedProperties properties = new SortedProperties();
                    properties.load(new InputStreamReader(file.getInputStream(), file.getCharset()));
                    readProperties(file.getNameWithoutExtension(), properties, nodes);
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

        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : translations.getLocales()) {
                    SortedProperties properties = new SortedProperties();
                    writeProperties(locale, properties, translations.getNodes(), "");

                    String fullPath = directoryPath + "/" + locale + "." + FILE_EXTENSION;
                    VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(new File(fullPath));

                    StringWriter content = new StringWriter();
                    properties.store(content, "I18n " + locale + " keys");

                    file.setBinaryContent(content.toString().getBytes(file.getCharset()));
                }

                // Successfully saved
                callback.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }

    private void writeProperties(String locale, Properties props, LocalizedNode node, String parentPath) {
        if(node.isLeaf() && !node.getKey().equals(LocalizedNode.ROOT_KEY)) {
            if(node.getValue().get(locale) != null) { // Translation is defined - track it
                String value = StringEscapeUtils.unescapeJava(node.getValue().get(locale));
                props.setProperty(parentPath, value);
            }

        } else {
            for(LocalizedNode children : node.getChildren()) {
                writeProperties(locale, props, children,
                        parentPath + (parentPath.isEmpty() ? "" : ".") + children.getKey());
            }
        }
    }

    private void readProperties(String locale, Properties props, LocalizedNode parent) {
        props.forEach((key, value) -> {
            List<String> sections = TranslationsUtil.getSections(String.valueOf(key));

            LocalizedNode node = parent;

            for (String section : sections) {
                LocalizedNode subNode = node.getChildren(section);

                if(subNode == null) {
                    subNode = new LocalizedNode(section, new ArrayList<>());
                    node.addChildren(subNode);
                }

                node = subNode;
            }

            Map<String, String> messages = node.getValue();
            String escapedValue = StringUtil.escapeControls(String.valueOf(value), true);
            messages.put(locale, escapedValue);
            node.setValue(messages);
        });
    }
}