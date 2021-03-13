package de.marhali.easyi18n.util;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import de.marhali.easyi18n.io.translator.JsonTranslatorIO;
import de.marhali.easyi18n.io.translator.TranslatorIO;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class IOUtil {

    public static TranslatorIO determineFormat(String directoryPath) {
        VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

        if(directory == null || directory.getChildren() == null) {
            throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
        }

        Optional<VirtualFile> any = Arrays.stream(directory.getChildren()).findAny();

        if(!any.isPresent()) {
            throw new IllegalStateException("Could not determine format");
        }

        switch (any.get().getFileType().getDefaultExtension().toLowerCase()) {
            case "json":
                return new JsonTranslatorIO();

            case "properties":
                throw new UnsupportedOperationException();

            default:
                throw new UnsupportedOperationException("Unsupported format: " +
                        any.get().getFileType().getDefaultExtension());
        }
    }
}