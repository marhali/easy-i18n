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
 * Preset for Ruby on Rails (I18n gem).
 *
 * <p>Matches {@code t('key')}, {@code translate('key')}, and {@code I18n.t('key')} calls
 * in Ruby source files. Translation files follow the standard Rails convention:
 * {@code config/locales/{locale}.yml} with the locale as the root YAML key.
 *
 * @author marhali
 */
public class RailsModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("rails"))
            .pathTemplate("$PROJECT_DIR$/config/locales/{locale}.yml")
            .fileCodec(FileCodec.YAML)
            // Rails YAML files have the locale as the top-level key: "en: { ... }"
            .fileTemplate("[{locale}][{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/app")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("I18n.t('{i18nKey}')")
            .editorRules()
            // t('key') — shorthand helper in controllers and views
            .editorRule(new EditorRule(
                "rails-t",
                Set.of(EditorLanguage.RUBY),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "t"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                10,
                false
            ))
            // translate('key') — verbose form
            .editorRule(new EditorRule(
                "rails-translate",
                Set.of(EditorLanguage.RUBY),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "translate"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
