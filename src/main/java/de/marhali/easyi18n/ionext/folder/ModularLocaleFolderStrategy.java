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
 * Modularized translation folder strategy by locale.
 * Directory => en dir => user.file / account.file
 * @author marhali
 */
public class ModularLocaleFolderStrategy extends FolderStrategy {

    public ModularLocaleFolderStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory) {
        List<TranslationFile> files = new ArrayList<>();

        for(VirtualFile localeModuleDir : localesDirectory.getChildren()) {
            if(localeModuleDir.isDirectory()) {
                String locale = localeModuleDir.getNameWithoutExtension();

                for(VirtualFile namespaceFile : localeModuleDir.getChildren()) {
                    if(super.isFileRelevant(namespaceFile)) {
                        files.add(new TranslationFile(namespaceFile, locale, namespaceFile.getNameWithoutExtension()));
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

        for(String locale : data.getLocales()) {
            for(String namespace : data.getRootNode().getChildren().keySet()) {
                VirtualFile vf = super.constructFile(localesPath + "/" + locale,
                        namespace + "." + type.getFileExtension());

                files.add(new TranslationFile(vf, locale, namespace));
            }
        }

        return files;
    }
}
