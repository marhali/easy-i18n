package de.marhali.easyi18n.io.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;
import de.marhali.easyi18n.model.TranslationNode;
import de.marhali.easyi18n.settings.ProjectSettings;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modularized translation folder strategy by locale.
 * Directory => en dir => user.file / account.file
 * @author marhali
 */
public class ModularLocaleFolderStrategy extends FolderStrategy {

    public ModularLocaleFolderStrategy(@NotNull ProjectSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory) {
        List<TranslationFile> files = new ArrayList<>();

        for(VirtualFile localeModuleDir : localesDirectory.getChildren()) {
            if(localeModuleDir.isDirectory()) {
                String locale = localeModuleDir.getNameWithoutExtension();
                files.addAll(findNamespaceFiles(locale, new KeyPath(), localeModuleDir));
            }
        }

        return files;
    }

    private List<TranslationFile> findNamespaceFiles(@NotNull String locale, @NotNull KeyPath ns, @NotNull VirtualFile dir) {
        List<TranslationFile> files = new ArrayList<>();

        for(VirtualFile namespaceFile : dir.getChildren()) {
            if(namespaceFile.isDirectory()) {
                if(settings.isIncludeSubDirs()) {
                    files.addAll(findNamespaceFiles(locale, new KeyPath(ns, namespaceFile.getName()), namespaceFile));
                }
                continue;
            }

            if(super.isFileRelevant(namespaceFile)) {
                files.add(new TranslationFile(namespaceFile, locale, new KeyPath(ns, namespaceFile.getNameWithoutExtension())));
            }
        }

        return files;
    }

    @Override
    public @NotNull List<TranslationFile> constructFolderStructure(
            @NotNull String localesPath, @NotNull ParserStrategyType type,
            @NotNull TranslationData data) throws IOException {

        List<TranslationFile> files = new ArrayList<>();

        for(String locale : data.getLocales()) {
            files.addAll(this.createNamespaceFiles(localesPath, locale, new KeyPath(), type, data.getRootNode()));
        }

        return files;
    }

    private List<TranslationFile> createNamespaceFiles(
            String localesPath, String locale, KeyPath path,
            ParserStrategyType type, TranslationNode node) throws IOException {

        List<TranslationFile> files = new ArrayList<>();

        for(Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String parentPath = localesPath + "/" + locale + "/" + String.join("/", path);

            if(super.exists(parentPath, entry.getKey())) { // Is directory - includeSubDirs
                files.addAll(createNamespaceFiles(localesPath, locale, new KeyPath(path, entry.getKey()), type, entry.getValue()));
                continue;
            }

            VirtualFile vf = super.constructFile(parentPath, entry.getKey() + "." + type.getFileExtension());
            files.add(new TranslationFile(vf, locale, new KeyPath(path, entry.getKey())));
        }

        return files;
    }
}
