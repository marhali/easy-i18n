package de.marhali.easyi18n.idea.help;

import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author marhali
 */
public final class WebUtil {

    /**
     * GitHub seems to have a maximum request url length limit of 8191 chars.
     */
    private static final int GITHUB_REQUEST_URL_LIMIT = 8191;

    private WebUtil() {}

    public static @NotNull String createOpenGitHubIssueUrl(@NotNull String title, @NotNull String labels, @NotNull String body) {
        String url = "https://github.com/marhali/easy-i18n/issues/new?title="
            + encodeParam(title) + "&labels=" + encodeParam(labels) + "&body=" + encodeParam(body);

        if (url.length() > GITHUB_REQUEST_URL_LIMIT) {
            // Trim url if length exceeds maximum limit constraint
            return url.substring(0, GITHUB_REQUEST_URL_LIMIT);
        }

        return url;
    }

    public static @NotNull String encodeParam(@NotNull String param) {
        return URLEncoder.encode(param, StandardCharsets.UTF_8);
    }
}
