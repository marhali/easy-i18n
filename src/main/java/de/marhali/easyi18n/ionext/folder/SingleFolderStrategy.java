package de.marhali.easyi18n.ionext.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Single directory translation folder strategy.
 * Every child is recognized as a file for a specific language.
 * Directory => en.file, de.file, fr.file
 * @author marhali
 */
public class SingleFolderStrategy extends FolderStrategy {

    public SingleFolderStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public List<TranslationFile> findFiles(@NotNull VirtualFile localesDirectory) {
        List<TranslationFile> files = new ArrayList<>();

        for(VirtualFile file : localesDirectory.getChildren()) {
            if(super.isFileRelevant(file)) {
                files.add(new TranslationFile(file, file.getNameWithoutExtension(), null));
            }
        }

        return files;
    }
}
