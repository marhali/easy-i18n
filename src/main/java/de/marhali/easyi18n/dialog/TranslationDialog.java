package de.marhali.easyi18n.dialog;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.Consumer;
import com.intellij.util.ui.FormBuilder;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.model.KeyPath;
import de.marhali.easyi18n.model.Translation;
import de.marhali.easyi18n.model.TranslationValue;
import de.marhali.easyi18n.model.action.TranslationUpdate;
import de.marhali.easyi18n.settings.ProjectSettings;
import de.marhali.easyi18n.settings.ProjectSettingsService;
import de.marhali.easyi18n.util.KeyPathConverter;

import org.apache.http.util.TextUtils;
import org.codehaus.jettison.json.JSONException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Base for add and edit translation dialogs.
 *
 * @author marhali
 */
abstract class TranslationDialog extends DialogWrapper {
    private String AI_PROXY_URL;
    private String AI_API_KEY;
    private String AI_MODAL;

    protected static final ResourceBundle bundle = ResourceBundle.getBundle("messages");

    protected final @NotNull Project project;
    protected final @NotNull ProjectSettings settings;
    protected final @NotNull KeyPathConverter converter;
    protected final @NotNull Translation origin;

    protected final JTextField keyField;
    protected final Map<String, JTextField> localeValueFields;

    private final Set<Consumer<TranslationUpdate>> callbacks;

    // 添加在类成员变量部分
    private final JBTextField jsonInputField;
    private final JButton jsonProcessButton;
    private final JBTextField textToTranslateField;
    private final JButton translateButton;

    /**
     * Constructs a new translation dialog.
     *
     * @param project Opened project
     * @param origin  Prefill translation
     */
    protected TranslationDialog(@NotNull Project project, @NotNull Translation origin) {
        super(project);
        AI_API_KEY = ProjectSettingsService.get(project).getState().getAiApiKey();
        AI_PROXY_URL = ProjectSettingsService.get(project).getState().getAiProxyUrl();
        AI_MODAL = ProjectSettingsService.get(project).getState().getAiModal();

        this.project = project;
        this.settings = ProjectSettingsService.get(project).getState();
        this.converter = new KeyPathConverter(settings);
        this.origin = origin;

        this.callbacks = new HashSet<>();

        // Fields
        TranslationValue value = origin.getValue();

        this.keyField = new JBTextField(converter.toString(origin.getKey()));
        this.localeValueFields = new HashMap<>();

        for (String locale : InstanceManager.get(project).store().getData().getLocales()) {
            localeValueFields.put(locale, new JBTextField(value != null ? value.get(locale) : null));
        }

        this.textToTranslateField = new JBTextField();
        this.translateButton = new JButton(bundle.getString("translation.translate"));
        this.jsonInputField = new JBTextField();
        this.jsonInputField.setText("{\"en\":\"English\",\"zh\":\"Chinese\"}");
        this.jsonProcessButton = new JButton(bundle.getString("translation.processJson"));

        translateButton.addActionListener(e -> translateText());
        jsonProcessButton.addActionListener(e -> processJsonInput());
    }

    public JTextField getKeyField() {
        return keyField;
    }

    /**
     * Registers a callback that is called on dialog close with the final state.
     * If the user aborts the dialog no callback is called.
     *
     * @param callback Callback to register
     */
    public void registerCallback(Consumer<TranslationUpdate> callback) {
        callbacks.add(callback);
    }

    /**
     * Implementation needs to handle exit
     *
     * @param exitCode See {@link com.intellij.openapi.ui.DialogWrapper} for exit codes
     * @return update conclusion, null if aborted
     */
    protected abstract @Nullable TranslationUpdate handleExit(int exitCode);

    /**
     * Opens the translation modal and applies the appropriate logic on modal close.
     * Internally, the {@link #handleExit(int)} method will be called to determine finalization logic.
     */
    public void showAndHandle() {
        init();
        show();

        int exitCode = getExitCode();
        TranslationUpdate update = handleExit(exitCode);

        if (update != null) {
            InstanceManager.get(project).processUpdate(update);
            callbacks.forEach(callback -> callback.consume(update));
        }
    }

    /**
     * Retrieve current modal state.
     *
     * @return Translation
     */
    protected @NotNull Translation getState() {
        KeyPath key = converter.fromString(keyField.getText());

        TranslationValue value = new TranslationValue();

        for (Map.Entry<String, JTextField> entry : localeValueFields.entrySet()) {
            value.put(entry.getKey(), entry.getValue().getText());
        }

        return new Translation(key, value);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(bundle.getString("translation.key"), keyField, true)
                .addComponent(createTranslateInputPanel(), 12)
                .addComponent(createJsonInputPanel(), 12)
                .addComponent(createLocalesPanel(), 12)
                .getPanel();

        panel.setMinimumSize(new Dimension(200, 150));

        return panel;
    }

    private JComponent createTranslateInputPanel() {
        JPanel translateInputPanel = new JPanel(new BorderLayout());
        translateInputPanel.add(textToTranslateField, BorderLayout.CENTER);
        translateInputPanel.add(translateButton, BorderLayout.EAST);

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), bundle.getString("translation.textToTranslate")));
        container.add(translateInputPanel, BorderLayout.CENTER);

        return container;
    }

    private boolean isRequesting = false;
    private void translateText() {
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        String textToTranslate = textToTranslateField.getText();
        if (textToTranslate.isEmpty()) {
            JOptionPane.showMessageDialog(null, bundle.getString("translation.emptyText"), bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE);
            isRequesting = false;
            return;
        }
        if (TextUtils.isEmpty(AI_API_KEY)) {
            JOptionPane.showMessageDialog(null, "Need API Key!", bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE);
            isRequesting = false;
            return;
        }

        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(AI_PROXY_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000); // 5 seconds for connection timeout
                connection.setReadTimeout(30000); // 30 seconds for read timeout
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + AI_API_KEY);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept", "text/event-stream");

                // Generate JSON schema
                JsonObject jsonSchema = new JsonObject();
                for (String locale : localeValueFields.keySet()) {
                    jsonSchema.addProperty(locale, "");
                }
                // Construct prompt
                String prompt = String.format(
                        "MUST Translate the following text into the supported languages in the provided JSON schema format. " +
                                "If the text is in a language other than English, infer the language and then translate. " +
                                "Do not provide me with markdown format, only plain text. " +
                                "Output the translation strictly as a JSON object without any additional formatting or comments.  " +
                                "Schema: %s\n\nText: %s",
                        jsonSchema.toString(), textToTranslate
                );

                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", AI_MODAL);
                requestBody.addProperty("stream", true);
                requestBody.addProperty("temperature", 0.2); // Lower value for more deterministic output
                requestBody.addProperty("top_p", 0.95); // Using nucleus sampling for more deterministic output
                JsonArray jsonArray = new JsonArray();
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.addProperty("role", "system");
                jsonObject1.addProperty("content", prompt);
                jsonArray.add(jsonObject1);
                JsonObject jsonObject2 = new JsonObject();
                jsonObject2.addProperty("role", "user");
                jsonObject2.addProperty("content", textToTranslate);
                jsonArray.add(jsonObject2);
                requestBody.add("messages", jsonArray);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                if (connection.getResponseCode() == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String line;
                        StringBuilder responseBuilder = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ")) {
                                line = line.substring(6);
                                if (line.equals("[DONE]")) break;

                                JsonObject jsonResponse = JsonParser.parseString(line).getAsJsonObject();
                                JsonArray choices = jsonResponse.getAsJsonArray("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    JsonObject choice = choices.get(0).getAsJsonObject();
                                    JsonObject delta = choice.getAsJsonObject("delta");
                                    if (delta != null && delta.has("content")) {
                                        String translatedText = delta.get("content").getAsString();
                                        responseBuilder.append(translatedText);
                                        SwingUtilities.invokeLater(() -> jsonInputField.setText(responseBuilder.toString()));
                                    }
                                }
                            }
                        }
                        SwingUtilities.invokeLater(() -> jsonInputField.setText(responseBuilder.toString().replaceAll("```","")));
                    }
                } else {
                    throw new IOException("Unexpected response code: " + connection.getResponseCode());
                }
            } catch (Exception e) {
//                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, bundle.getString("translation.translationFailed"), bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE));
                HttpURLConnection finalConnection = connection;
                SwingUtilities.invokeLater(() -> {
                    try {
                        JOptionPane.showMessageDialog(null, finalConnection != null ? finalConnection.getResponseMessage() : e.getLocalizedMessage(), bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, e.getLocalizedMessage(), bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE);
                    }
                });
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                isRequesting = false;
            }
        });
    }

    private JComponent createLocalesPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        for (Map.Entry<String, JTextField> localeEntry : localeValueFields.entrySet()) {
            builder.addLabeledComponent(localeEntry.getKey(), localeEntry.getValue(), 6, true);
        }

        JScrollPane scrollPane = new JBScrollPane(builder.getPanel());

        scrollPane.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), bundle.getString("translation.locales")));

        return scrollPane;
    }

    private JComponent createJsonInputPanel() {
        JPanel jsonInputPanel = new JPanel(new BorderLayout());
        jsonInputPanel.add(jsonInputField, BorderLayout.CENTER);
        jsonInputPanel.add(jsonProcessButton, BorderLayout.EAST);

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createTitledBorder(
                new EtchedBorder(), bundle.getString("translation.jsonInput")));
        container.add(jsonInputPanel, BorderLayout.CENTER);

        return container;
    }

    private void processJsonInput() {
        String jsonText = jsonInputField.getText();

        try {
            Map<String, String> jsonMap = new Gson().fromJson(jsonText, new TypeToken<Map<String, String>>() {
            }.getType());
            jsonMap.forEach((locale, value) -> {
                JTextField field = localeValueFields.get(locale);
                if (field != null) {
                    field.setText(value);
                }
            });
        } catch (JsonSyntaxException e) {
            JOptionPane.showMessageDialog(null, bundle.getString("translation.invalidJson"), bundle.getString("translation.error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}