package de.marhali.easyi18n.io.folder;

import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.parser.ParserStrategyType;
import de.marhali.easyi18n.model.SettingsState;
import de.marhali.easyi18n.model.TranslationData;
import de.marhali.easyi18n.model.TranslationFile;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Single directory translation folder strategy.
 * Every child is recognized as a file for a specific language.
 * Directory => en.file, de.file, fr.file
 *
 * @author marhali
 */
public class SingleFolderStrategy extends FolderStrategy {

    public SingleFolderStrategy(@NotNull SettingsState settings) {
        super(settings);
    }

    @Override
    public @NotNull List<TranslationFile> analyzeFolderStructure(@NotNull VirtualFile localesDirectory) {
        List<TranslationFile> files = new ArrayList<>();

        for (VirtualFile file : localesDirectory.getChildren()) {
            if (super.isFileRelevant(file)) {
                files.add(new TranslationFile(file, file.getNameWithoutExtension(), null));
            }
        }

        return files;
    }

    @Override
    public @NotNull List<TranslationFile> constructFolderStructure(
            @NotNull String localesPath, @NotNull ParserStrategyType type,
            @NotNull TranslationData data) throws IOException {

        List<TranslationFile> files = new ArrayList<>();

        for (String locale : data.getLocales()) {
            VirtualFile vf = super.constructFile(localesPath,
                    locale + "." + type.getFileExtension());

            files.add(new TranslationFile(vf, locale, null));
        }

        return files;
    }
}
