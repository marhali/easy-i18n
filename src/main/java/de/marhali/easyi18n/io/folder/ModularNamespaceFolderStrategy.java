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
import java.util.Set;

/**
 * Modular translation folder strategy by namespace.
 * Directory => user dir => en.file / de.file
 * @author marhali
 */
public class ModularNamespaceFolderStrategy extends FolderStrategy {

    public ModularNamespaceFolderStrategy(@NotNull ProjectSettings settings) {
        super(settings);
    }

    @Override
    public @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory) {
        return new ArrayList<>(findLocaleFiles(new KeyPath(), localesDirectory));
    }

    private List<TranslationFile> findLocaleFiles(KeyPath ns, VirtualFile dir) {
        List<TranslationFile> files = new ArrayList<>();

        for (VirtualFile localeFile : dir.getChildren()) {
            if(localeFile.isDirectory()) {
                if(ns.isEmpty() || settings.isIncludeSubDirs()) {
                    files.addAll(findLocaleFiles(new KeyPath(ns, localeFile.getName()), localeFile));
                }
                continue;
            }

            if(super.isFileRelevant(localeFile)) {
                files.add(new TranslationFile(localeFile, localeFile.getNameWithoutExtension(), ns));
            }
        }

        return files;
    }

    @Override
    public @NotNull List<TranslationFile> constructFolderStructure(
            @NotNull String localesPath, @NotNull ParserStrategyType type,
            @NotNull TranslationData data) throws IOException {

        return new ArrayList<>(this.createLocaleFiles(
                localesPath, data.getLocales(), new KeyPath(), type, data.getRootNode()));
    }

    private List<TranslationFile> createLocaleFiles(String localesPath, Set<String> locales, KeyPath path, ParserStrategyType type, TranslationNode node) throws IOException {
        List<TranslationFile> files = new ArrayList<>();

        for (Map.Entry<String, TranslationNode> entry : node.getChildren().entrySet()) {
            String parentPath = localesPath + "/" + String.join("/", path);

            // Root-Node or is directory(includeSubDirs)
            if(path.isEmpty() || super.exists(parentPath, entry.getKey())) {
                files.addAll(createLocaleFiles(localesPath, locales, new KeyPath(path, entry.getKey()), type, entry.getValue()));
                continue;
            }

            for (String locale : locales) {
                VirtualFile vf = super.constructFile(parentPath, locale + "." + type.getFileExtension());
                files.add(new TranslationFile(vf, locale, path));
            }
        }

        return files;
    }
}
