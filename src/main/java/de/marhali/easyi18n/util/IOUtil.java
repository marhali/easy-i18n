package de.marhali.easyi18n.util;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.io.implementation.JsonTranslatorIO;
import de.marhali.easyi18n.io.implementation.PropertiesTranslatorIO;
import de.marhali.easyi18n.io.TranslatorIO;

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
}