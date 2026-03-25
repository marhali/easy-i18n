package de.marhali.easyi18n.core.domain.config.preset.impl;

import de.marhali.easyi18n.core.domain.config.FileCodec;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.config.preset.PresetProvider;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Preset for Laravel (JSON translation strings).
 *
 * <p>Matches {@code __('key')}, {@code trans('key')}, and {@code trans_choice('key', n)}
 * calls in PHP files. Targets the JSON translation file layout introduced in Laravel 5.4+:
 * {@code resources/lang/{locale}.json}.
 *
 * @author marhali
 */
public class LaravelModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("laravel"))
            .pathTemplate("$PROJECT_DIR$/resources/lang/{locale}.json")
            .fileCodec(FileCodec.JSON)
            .fileTemplate("[{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/app")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("__('{i18nKey}')")
            .editorRules()
            // __('key') — global translation helper
            .editorRule(new EditorRule(
                "laravel-double-underscore",
                Set.of(EditorLanguage.PHP),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "__"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                10,
                false
            ))
            // trans('key') — alias helper
            .editorRule(new EditorRule(
                "laravel-trans",
                Set.of(EditorLanguage.PHP),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "trans"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            // trans_choice('key', n) — pluralisation helper
            .editorRule(new EditorRule(
                "laravel-trans-choice",
                Set.of(EditorLanguage.PHP),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "trans_choice"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
