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
 * Preset for Spring Boot (MessageSource / ResourceBundle).
 *
 * <p>Matches {@code messageSource.getMessage('key', ...)} and
 * {@code bundle.getString('key')} calls in Java and Kotlin files.
 * Translation files use the standard Spring naming convention:
 * {@code src/main/resources/messages_{locale}.properties}.
 *
 * @author marhali
 */
public class SpringBootModulePreset implements PresetProvider<ProjectConfigModule> {

    @Override
    public @NotNull ProjectConfigModule applyPreset(@Nullable ProjectConfigModule previousState) {
        return ProjectConfigModule.builder()
            .id(previousState != null ? previousState.id() : new ModuleId("spring-boot"))
            .pathTemplate("$PROJECT_DIR$/src/main/resources/messages_{locale}.properties")
            .fileCodec(FileCodec.PROPERTIES)
            .fileTemplate("[{fileKey}]")
            .keyTemplate("{fileKey:.}")
            .rootDirectory("$PROJECT_DIR$/src/main")
            .defaultKeyPrefixes()
            .editorFlavorTemplate("messageSource.getMessage(\"{i18nKey}\", null, locale)")
            .editorRules()
            // messageSource.getMessage('key', ...) — Spring MessageSource
            .editorRule(new EditorRule(
                "spring-getMessage",
                Set.of(EditorLanguage.JAVA, EditorLanguage.KOTLIN),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "getMessage"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                10,
                false
            ))
            // bundle.getString('key') — standard Java ResourceBundle
            .editorRule(new EditorRule(
                "spring-getString",
                Set.of(EditorLanguage.JAVA, EditorLanguage.KOTLIN),
                TriggerKind.CALL_ARGUMENT,
                List.of(
                    EditorRuleConstraint.exact(RuleConstraintType.CALLABLE_NAME, "getString"),
                    EditorRuleConstraint.exact(RuleConstraintType.ARGUMENT_INDEX, "0")
                ),
                0,
                false
            ))
            .build();
    }
}
