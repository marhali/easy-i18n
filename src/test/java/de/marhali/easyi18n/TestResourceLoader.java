package de.marhali.easyi18n;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author marhali
 */
public final class TestResourceLoader {
    /**
     * Retrieves the contents from a test resource file as {@link String} UTF-8 encoded.
     * @param resourcePath Path to translation resource (relative from src/test/resources)
     * @return Resource file content
     * @throws IOException Could not read file
     */
    public static @NotNull String getAsString(String resourcePath) throws IOException {
        try (InputStream stream = TestResourceLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IOException("Test resource not found: " + resourcePath);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
