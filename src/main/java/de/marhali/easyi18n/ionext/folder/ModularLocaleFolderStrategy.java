package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationFile;

import org.jetbrains.annotations.NotNull;

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
    public List<TranslationFile> findFiles(@NotNull VirtualFile localesDirectory) {
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
}
