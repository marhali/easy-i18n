package de.marhali.easyi18n.core.domain.config;

import de.marhali.easyi18n.core.domain.model.LocaleId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builder pattern to construct a {@link ProjectConfig}.
 *
 * @author marhali
 */
public class ProjectConfigBuilder {

    private boolean keyComment;
    private boolean sorting;
    private LocaleId previewLocale;
    private List<ProjectConfigModule> modules;

    protected ProjectConfigBuilder() {}

    protected ProjectConfigBuilder(@NotNull ProjectConfig config) {
        this.keyComment = config.keyComment();
        this.sorting = config.sorting();
        this.previewLocale = config.previewLocale();
        this.modules = config.modules().values().stream()
            .map(ProjectConfigModule::toBuilder)
            .map(ProjectConfigModuleBuilder::build)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public @NotNull ProjectConfigBuilder keyComment(boolean keyComment) {
        this.keyComment = keyComment;
        return this;
    }

    public @NotNull ProjectConfigBuilder sorting(boolean sorting) {
        this.sorting = sorting;
        return this;
    }

    public @NotNull ProjectConfigBuilder previewLocale(@NotNull LocaleId previewLocale) {
        this.previewLocale = previewLocale;
        return this;
    }

    public @NotNull ProjectConfigBuilder modules(@NotNull List<ProjectConfigModule> modules) {
        this.modules = modules;
        return this;
    }

    public @NotNull ProjectConfigBuilder modules() {
        return modules(new ArrayList<>());
    }

    public @NotNull ProjectConfigBuilder module(@NotNull Function<ProjectConfigModuleBuilder, ProjectConfigModule> builder) {
        modules.add(builder.apply(ProjectConfigModule.builder()));
        return this;
    }

    // Last

    public @NotNull ProjectConfig build() {
        return new ProjectConfig(
            keyComment,
            sorting,
            previewLocale,
            modules.stream()
                .collect(Collectors.toMap(ProjectConfigModule::id, e -> e))
        );
    }
}
