package de.marhali.easyi18n.idea.help;

import com.intellij.openapi.project.Project;
import de.marhali.easyi18n.core.domain.config.ProjectConfig;
import de.marhali.easyi18n.core.domain.config.ProjectConfigModule;
import de.marhali.easyi18n.core.domain.model.I18nKeyPrefix;
import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.core.domain.rules.EditorRule;
import de.marhali.easyi18n.core.domain.rules.EditorRuleConstraint;
import de.marhali.easyi18n.idea.config.state.EditorRuleState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author marhali
 */
public final class PrettyConfigUtil {

    private PrettyConfigUtil() {}

    public static void appendConfig(@NotNull StringBuilder builder, @NotNull ProjectConfig config, @NotNull Project project) {
        builder.append("# Config\n");
        builder.append("- Sorting: `").append(config.sorting()).append("`\n");
        builder.append("- Preview Locale: `").append(config.previewLocale().tag()).append("`\n");
        builder.append("## Modules\n");

        if (config.modules().isEmpty()) {
            builder.append(" - empty -\n");
        } else {
            for (Map.Entry<@NotNull ModuleId, @NotNull ProjectConfigModule> entry : config.modules().entrySet()) {
                appendModuleConfig(builder, entry.getValue(), project);
            }
        }
    }

    public static void appendModuleConfig(@NotNull StringBuilder builder, @NotNull ProjectConfigModule config, @NotNull Project project) {
        builder.append("- ").append(config.id().name()).append("\n");
        builder.append("  - Path: `").append(MacroUtil.collapsePath(project, config.pathTemplate())).append("`\n");
        builder.append("  - File: `").append(config.fileTemplate()).append("`\n");
        builder.append("  - Key: `").append(config.keyTemplate()).append("`\n");
        builder.append("  - Dir: `").append(MacroUtil.collapsePath(project, config.rootDirectory())).append("`\n");
        builder.append("  - Prefixes: `").append(!config.defaultKeyPrefixes().isEmpty()
                ? String.join(";", config.defaultKeyPrefixes().stream().map(I18nKeyPrefix::canonicalPrefix).collect(Collectors.toSet()))
                : "- empty -")
            .append("`\n");
        builder.append("  - Flavor: `").append(config.editorFlavorTemplate()).append("`\n");
        builder.append("  - Rules\n");

        if (config.editorRules().isEmpty()) {
            builder.append("    - - empty -\n");
        } else {
            for (EditorRule rule : config.editorRules()) {
                appendModuleRule(builder, rule);
            }
        }
    }

    public static void appendModuleRule(@NotNull StringBuilder builder, @NotNull EditorRule rule) {
        builder.append("    - `")
            .append(rule.id())
            .append("` - `")
            .append(String.join(";", rule.languages().stream().map(EditorLanguage::name).collect(Collectors.toSet())))
            .append("` - `")
            .append(rule.triggerKind())
            .append("` - `")
            .append(rule.priority())
            .append("` - `")
            .append(rule.excludeRule())
            .append("`\n");

        for (EditorRuleConstraint constraint : rule.constraints()) {
            appendModuleRuleConstraint(builder, constraint);
        }
    }

    public static void appendModuleRuleConstraint(@NotNull StringBuilder builder, @NotNull EditorRuleConstraint constraint) {
        builder.append("      - `")
            .append(constraint.type())
            .append("` - `")
            .append(constraint.value())
            .append("` - `")
            .append(constraint.matchMode())
            .append("` - `")
            .append(constraint.negated())
            .append("`\n");
    }
}
