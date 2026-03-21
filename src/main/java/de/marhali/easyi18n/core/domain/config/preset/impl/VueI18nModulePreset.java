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
 * Preset for Vue I18n (v9 / Composition API).
 *
 * <p>Matches {@code t('key')} (Composition API) and {@code $t('key')} (Options API / templates)
 * in Vue, JavaScript, and TypeScript files. Translation files are stored per locale as flat JSON:
 * {@code src/locales/{locale}.json}.
 *
 * @author marhali
 */
public class VueI18nModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("vue-i18n"))
            .pathTemplate("$PROJECT_DIR$/src/locales/{locale}.json")
            .fileCodec(FileCodec.JSON)
            .fileTemplate("[{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/src")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("t('{i18nKey}')")
            .editorRules()
            // t('key') — Composition API useI18n hook
            .editorRule(new EditorRule(
                "vue-i18n-t",
                Set.of(EditorLanguage.JAVASCRIPT, EditorLanguage.TYPESCRIPT, EditorLanguage.VUE),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "t"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                10,
                false
            ))
            // $t('key') — Options API / template inline expressions
            .editorRule(new EditorRule(
                "vue-i18n-dollar-t",
                Set.of(EditorLanguage.JAVASCRIPT, EditorLanguage.TYPESCRIPT, EditorLanguage.VUE),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "$t"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
