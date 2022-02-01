package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationFile;

import org.jetbrains.annotations.NotNull;

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
    public List<TranslationFile> findFiles(@NotNull VirtualFile localesDirectory) {
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
}
