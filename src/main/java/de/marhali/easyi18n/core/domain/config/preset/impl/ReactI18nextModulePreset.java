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
 * Preset for React i18next / next-i18next (namespace layout).
 *
 * <p>Matches {@code t('key')} from the {@code useTranslation} hook in JavaScript and TypeScript
 * files. Translation files are organized by locale and namespace:
 * {@code public/locales/{locale}/{pathNamespace}.json}. The full key exposed to the editor
 * is {@code namespace:subKey}.
 *
 * @author marhali
 */
public class ReactI18nextModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("react-i18next"))
            .pathTemplate("$PROJECT_DIR$/public/locales/{locale}/{pathNamespace}.json")
            .fileCodec(FileCodec.JSON)
            .fileTemplate("[{fileKey}]")
            // namespace prefix separates files: "common:greeting"
            .keyTemplate("{pathNamespace::[^:]+}:{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/src")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("t('{i18nKey}')")
            .editorRules()
            // t('key') — useTranslation hook, first argument is the key
            .editorRule(new EditorRule(
                "i18next-t",
                Set.of(EditorLanguage.JAVASCRIPT, EditorLanguage.TYPESCRIPT),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "t"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
