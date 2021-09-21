package de.marhali.easyi18n.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.io.implementation.*;
import de.marhali.easyi18n.io.TranslatorIO;

import de.marhali.easyi18n.service.SettingsService;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * IO operations utility.
 * @author marhali
 */
public class IOUtil {

    /**
     * Determines the {@link TranslatorIO} which should be used for the specified directoryPath
     * @param project Current intellij project
     * @param directoryPath The full path to the parent directory which holds the translation files
     * @return IO handler to use for file operations
     */
    public static TranslatorIO determineFormat(@NotNull Project project, @NotNull String directoryPath) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

        if(directory == null || directory.getChildren() == null) {
            throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
        }

        VirtualFile[] children = directory.getChildren();

        for(VirtualFile file : children) {
            if(file.isDirectory()) { // Modularized locale files
                // ATM we only support modularized JSON files
                return new ModularizedJsonTranslatorIO();
            }

            if(!isFileRelevant(project, file)) {
                continue;
            }

            switch(file.getFileType().getDefaultExtension().toLowerCase()) {
                case "json":
                    return new JsonTranslatorIO();
                case "properties":
                    return new PropertiesTranslatorIO();
                case "yml":
                    return new YamlTranslatorIO();
                default:
                    System.err.println("Unsupported i18n locale file format: "
                            + file.getFileType().getDefaultExtension());
            }
        }

        throw new IllegalStateException("Could not determine i18n format. At least one locale file must be defined");
    }

    /**
     * Checks if the provided file matches the file pattern specified by configuration
     * @param project Current intellij project
     * @param file File to check
     * @return True if relevant otherwise false
     */
    public static boolean isFileRelevant(@NotNull Project project, @NotNull VirtualFile file) {
        String pattern = SettingsService.getInstance(project).getState().getFilePattern();
        return file.getName().matches(pattern);
    }
}