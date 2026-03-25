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
 * Preset for Angular ngx-translate.
 *
 * <p>Matches {@code translate.instant('key')} and {@code translate.get('key')} calls
 * in TypeScript files. Translation files follow the standard ngx-translate layout:
 * {@code src/assets/i18n/{locale}.json}.
 *
 * @author marhali
 */
public class AngularNgxTranslateModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("angular-ngx-translate"))
            .pathTemplate("$PROJECT_DIR$/src/assets/i18n/{locale}.json")
            .fileCodec(FileCodec.JSON)
            .fileTemplate("[{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/src")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("this.translate.instant(\"{i18nKey}\")")
            .editorRules()
            // translate.instant('key') — synchronous lookup
            .editorRule(new EditorRule(
                "ngx-instant",
                Set.of(EditorLanguage.TYPESCRIPT),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "instant"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                10,
                false
            ))
            // translate.get('key') — observable lookup
            .editorRule(new EditorRule(
                "ngx-get",
                Set.of(EditorLanguage.TYPESCRIPT),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "get"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
