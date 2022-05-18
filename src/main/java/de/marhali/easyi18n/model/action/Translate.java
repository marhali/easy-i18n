package de.marhali.easyi18n.model.action;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.util.NotificationHelper;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Represents a translation into another language
 * @author DatzAtWork
 */
public class Translate {

    private static final String GLOSBE_URI = "https://translator-api.glosbe.com/translateByLangWithScoreXXXXX?sourceLang=%s&targetLang=%s";

    private final @NotNull Project project;
    private final @NotNull String textToTranslate;
    private final @NotNull String targetLocale;
    private final @NotNull String sourceLocale;

    private final CompletableFuture<HttpResponse<String>> responseFuture;

    public Translate(@NotNull Project project, String targetLocale, String sourceLocale, String textToTranslate) {
        this.project = project;
        this.sourceLocale = sourceLocale;
        this.textToTranslate = textToTranslate;
        this.targetLocale = targetLocale;

        responseFuture = sendRequest(targetLocale, sourceLocale, textToTranslate);
    }

    private CompletableFuture<HttpResponse<String>> sendRequest(String targetLocale, String sourceLocale, String textToTranslate) {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(String.format(GLOSBE_URI, sourceLocale, targetLocale)))
                .POST(HttpRequest.BodyPublishers.ofString(textToTranslate))
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(Function.identity());
    }

    public Optional<String> getTranslation() {
        final HttpResponse<String> response = this.responseFuture.join();
        if(response.statusCode()==200) {
            final JsonElement resultJson = JsonParser.parseString(response.body());
            return Optional.of(((JsonObject) resultJson).get("translation").getAsString());
        }
        NotificationHelper.createTranslatorServiceErrorNotification(project, response.statusCode(), response.body());
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "Translate{" +
                "textToTranslate=" + textToTranslate +
                ", targetLocale=" + targetLocale +
                ", sourceLocale=" + sourceLocale + '}';
    }
}
