package de.marhali.easyi18n.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.io.implementation.JsonTranslatorIO;
import de.marhali.easyi18n.io.implementation.ModularizedJsonTranslatorIO;
import de.marhali.easyi18n.io.implementation.PropertiesTranslatorIO;
import de.marhali.easyi18n.io.TranslatorIO;

import de.marhali.easyi18n.service.SettingsService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * IO operations utility.
 * @author marhali
 */
public class IOUtil {

    /**
     * Determines the {@link TranslatorIO} which should be used for the specified directoryPath
     * @param directoryPath The full path to the parent directory which holds the translation files
     * @return IO handler to use for file operations
     */
    public static TranslatorIO determineFormat(@NotNull String directoryPath) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

        if(directory == null || directory.getChildren() == null) {
            throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
        }

        Optional<VirtualFile> any = Arrays.stream(directory.getChildren()).findAny();

        if(!any.isPresent()) {
            throw new IllegalStateException("Could not determine i18n format. At least one locale file must be defined");
        }

        // Split files - Should be always JSON
        if(any.get().isDirectory()) {
            return new ModularizedJsonTranslatorIO();
        }

        switch (any.get().getFileType().getDefaultExtension().toLowerCase()) {
            case "json":
                return new JsonTranslatorIO();

            case "properties":
                return new PropertiesTranslatorIO();

            default:
                throw new UnsupportedOperationException("Unsupported i18n locale file format: " +
                        any.get().getFileType().getDefaultExtension());
        }
    }

    /**
     * Checks if the provided file matches the file pattern specified by configuration
     * @param project Current intellij project
     * @param file File to check
     * @return True if relevant otherwise false
     */
    public static boolean isFileRelevant(Project project, VirtualFile file) {
        String pattern = SettingsService.getInstance(project).getState().getFilePattern();
        return file.getName().matches(pattern);
    }
}