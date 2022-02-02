package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.ParserStrategyType;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Modular translation folder strategy by namespace.
 * Directory => user dir => en.file / de.file
 * @author marhali
 */
public class ModularNamespaceFolderStrategy extends FolderStrategy {

    public ModularNamespaceFolderStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory) {
        List<TranslationFile> files = new ArrayList<>();

        for(VirtualFile namespaceModuleDir : localesDirectory.getChildren()) {
            if(namespaceModuleDir.isDirectory()) {
                String namespace = namespaceModuleDir.getNameWithoutExtension();

                for(VirtualFile localeFile : namespaceModuleDir.getChildren()) {
                    if(super.isFileRelevant(localeFile)) {
                        files.add(new TranslationFile(localeFile, localeFile.getNameWithoutExtension(), namespace));
                    }
                }
            }
        }

        return files;
    }

    @Override
    public @NotNull List<TranslationFile> constructFolderStructure(
            @NotNull String localesPath, @NotNull ParserStrategyType type,
            @NotNull TranslationData data) throws IOException {

        List<TranslationFile> files = new ArrayList<>();

        for(String namespace : data.getRootNode().getChildren().keySet()) {
            for(String locale : data.getLocales()) {
                VirtualFile vf = super.constructFile(localesPath + "/" + namespace,
                        locale + "." + type.getFileExtension());

                files.add(new TranslationFile(vf, locale, namespace));
            }
        }

        return files;
    }
}
