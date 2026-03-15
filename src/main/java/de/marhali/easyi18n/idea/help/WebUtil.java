package de.marhali.easyi18n.idea.help;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author marhali
 */
public final class WebUtil {

    private WebUtil() {}

    public static @NotNull String createOpenGitHubIssueUrl(@NotNull String title, @NotNull String labels, @NotNull String body) {
        String url = "https://github.com/marhali/easy-i18n/issues/new?title="
            + encodeParam(title) + "&labels=" + encodeParam(labels) + "&body=" + encodeParam(body);

        if(url.length() > 8201) {
            // Consider github request url limit
            return url.substring(0, 8201);
        }

        return url;
    }

    public static @NotNull String encodeParam(@NotNull String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
