package de.marhali.easyi18n.idea.dialog;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.*;
import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.idea.messages.PluginBundle;
import de.marhali.easyi18n.idea.service.I18nProjectService;
import de.marhali.easyi18n.idea.service.PluginExecutorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Dialog to add or edit translation. On edit mode, translation could be also deleted.
 *
 * @author marhali
 */
public class TranslationDialog extends DialogWrapper {

    private final @NotNull TranslationDialogMode mode;
    private final @NotNull NullableI18nEntry originEntry;

    private final @NotNull DialogViewModel vm;

    private @Nullable JBTextField keyField;
    private @Nullable JBLabel keyFieldHint;
    private @Nullable JBScrollPane valuesByLocalePane;
    private @Nullable Map<@NotNull LocaleId, @NotNull JBTextArea> valuesByLocale;

    /**
     * Dialogs should be instantiated by using the factory.
     *
     * @see TranslationDialogFactory
     */
    protected TranslationDialog(
        @NotNull Project project,
        @NotNull ModuleId moduleId,
        @NotNull TranslationDialogMode mode,
        @NotNull NullableI18nEntry originEntry
    ) {
        super(project);

        this.mode = mode;

        var projectService = project.getService(I18nProjectService.class);
        var executorService = project.getService(PluginExecutorService.class);

        this.vm = new DialogViewModel(projectService, executorService, moduleId, ModalityState.stateForComponent(getRootPane()), getDisposable(), (o) -> isDisposed());

        this.originEntry = originEntry;

        // Keep last
        setTitle(
            mode == TranslationDialogMode.ADD
            ? PluginBundle.message("dialog.translation.title.add", moduleId.name())
            : PluginBundle.message("dialog.translation.title.edit", moduleId.name())
        );

        init();

        evaluateOKActionEnabled();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();

        // Key field
        keyField = new JBTextField();
        keyField.setToolTipText(PluginBundle.message("dialog.translation.key.tooltip"));
        keyField.getEmptyText().setText(PluginBundle.message("dialog.translation.key.placeholder"));
        builder.addLabeledComponent(PluginBundle.message("dialog.translation.key.label"), keyField, true);

        keyField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent documentEvent) {
                evaluateOKActionEnabled();
                if (keyField.isEnabled() && isOKActionEnabled()) {
                    resetI18nKeyExists();
                    vm.checkI18nKeyExistsAsync(
                        I18nKey.of(keyField.getText()),
                        (exists) -> handleI18nKeyExists(exists),
                        (throwable) -> handleThrowable(throwable)
                    );
                }
            }
        });

        // Key hint (e.g. key already exists)
        keyFieldHint = new JBLabel(PluginBundle.message("dialog.translation.key.hint.duplicate"));
        keyFieldHint.setForeground(JBColor.ORANGE);
        keyFieldHint.setVisible(false);
        keyFieldHint.setFont(JBFont.small());
        keyFieldHint.setBorder(JBUI.Borders.empty(2, 8));
        builder.addComponent(keyFieldHint);

        // Locales scroll pane
        valuesByLocalePane = new JBScrollPane();
        valuesByLocalePane.setBorder(JBUI.Borders.empty());
        valuesByLocalePane.setFocusable(false);
        builder.addLabeledComponent(PluginBundle.message("dialog.translation.locales.title"), valuesByLocalePane, 16,true);

        var panel = builder.getPanel();
        panel.setMinimumSize(new JBDimension(200, 150));
        panel.setPreferredSize(new JBDimension(400, 300));

        // Retrieve module specific set of locales
        vm.loadLocalesAsync(
            (localeIds) -> {
                buildLocalesPanel(localeIds);
                applyOrigin();
            },
            this::handleThrowable
        );

        return panel;
    }

    @Override
    protected Action @NotNull [] createLeftSideActions() {
        if (mode == TranslationDialogMode.EDIT) {
            // Add option to delete translation if in edit mode
            return new Action[]{
                new DialogAction.Delete(this::doDeleteAction)
            };
        }

        return super.createLeftSideActions();
    }

    @Override
    protected void doOKAction() {
        Objects.requireNonNull(keyField, "keyField must not be null");
        Objects.requireNonNull(valuesByLocale, "valuesByLocale must not be null");

        I18nKey key = I18nKey.of(keyField.getText());
        Map<LocaleId, I18nValue> values = valuesByLocale.entrySet().stream()
            .filter(entry -> !entry.getValue().getText().isBlank())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                (entry) -> I18nValue.fromInputString(entry.getValue().getText())
                ));
        I18nContent content = new I18nContent(values, null);
        I18nEntry entry = new I18nEntry(key, content);

        vm.saveAsync(
            mode,
            entry,
            originEntry,
            (_void) -> super.doOKAction(),
            this::handleThrowable
        );
    }

    protected void doDeleteAction() {
        Objects.requireNonNull(originEntry.key(), "originEntry.key must not be null");

        // Should only be possible in edit mode
        assert mode == TranslationDialogMode.EDIT;

        vm.deleteAsync(
            originEntry.key(),
            (_void) -> super.close(DialogAction.Delete.EXIT_CODE),
            this::handleThrowable
        );
    }

    private void buildLocalesPanel(@NotNull Set<@NotNull LocaleId> localeIds) {
        Objects.requireNonNull(valuesByLocalePane, "valuesByLocalePane must not be null");

        valuesByLocale = new HashMap<>(localeIds.size());
        FormBuilder builder = FormBuilder.createFormBuilder();

        if (localeIds.isEmpty()) {
            var emptyLabel = new JBLabel(PluginBundle.message("dialog.translation.locales.empty"));
            emptyLabel.setForeground(JBColor.red);
            emptyLabel.setFont(JBFont.small());
            emptyLabel.setBorder(JBUI.Borders.empty(2, 8));
            builder.addComponent(emptyLabel);
        } else {
            for (LocaleId localeId : localeIds) {
                var field = new JBTextArea();
                field.setLineWrap(true);
                field.setWrapStyleWord(true);
                field.setBorder(BorderFactory.createTitledBorder(localeId.tag()));
                field.setLocale(Locale.forLanguageTag(localeId.tag()));
                valuesByLocale.put(localeId, field);
                builder.addComponent(field, 4);
            }
        }

        builder.addComponentFillVertically(new JPanel(), 0);
        valuesByLocalePane.setViewportView(builder.getPanel());
    }

    private void applyOrigin() {
        Objects.requireNonNull(keyField, "keyField must not be null");
        Objects.requireNonNull(valuesByLocale, "valuesByLocale must not be null");

        if (originEntry.key() != null) {
            keyField.setEnabled(false);
            keyField.setText(originEntry.key().canonical());
            keyField.setEnabled(true);
            evaluateOKActionEnabled();
        }

        if (originEntry.content() != null) {
            for (Map.Entry<@NotNull LocaleId, @NotNull I18nValue> entry : originEntry.content().values().entrySet()) {
                var field = valuesByLocale.get(entry.getKey());

                if (field == null) {
                    setErrorText(PluginBundle.message("dialog.translation.locales.unknown", entry.getKey()));
                } else {
                    field.setText(entry.getValue().toInputString());
                }
            }
        }
    }

    private void evaluateOKActionEnabled() {
        Objects.requireNonNull(keyField, "keyField must not be null");

        setOKActionEnabled(!keyField.getText().isBlank());
    }

    private void resetI18nKeyExists() {
        Objects.requireNonNull(keyFieldHint, "keyFieldHint must not be null");

        keyFieldHint.setVisible(false);
    }

    private void handleI18nKeyExists(boolean exists) {
        Objects.requireNonNull(keyFieldHint, "keyFieldHint must not be null");

        keyFieldHint.setVisible(exists);
    }

    private void handleThrowable(@NotNull Throwable throwable) {
        setErrorText(PluginBundle.message("error.operation.details", throwable.getLocalizedMessage()));
    }
}
