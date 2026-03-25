package de.marhali.easyi18n.core.application.service;

import de.marhali.easyi18n.core.domain.model.ModuleId;
import de.marhali.easyi18n.core.domain.rules.CompiledRules;
import de.marhali.easyi18n.core.domain.rules.EditorRule;
import de.marhali.easyi18n.core.domain.rules.I18nRuleEngine;
import de.marhali.easyi18n.core.domain.rules.RuleCompiler;
import de.marhali.easyi18n.core.ports.ProjectConfigPort;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for module-specific {@link I18nRuleEngine}'s.
 *
 * @author marhali
 */
public class CachedModuleRules {

    private final @NotNull ProjectConfigPort projectConfigPort;
    private final @NotNull Map<@NotNull ModuleId, @NotNull I18nRuleEngine> cache;

    public CachedModuleRules(@NotNull ProjectConfigPort projectConfigPort) {
        this(projectConfigPort, new ConcurrentHashMap<>());
    }

    public CachedModuleRules(@NotNull ProjectConfigPort projectConfigPort, @NotNull Map<@NotNull ModuleId, @NotNull I18nRuleEngine> cache) {
        this.projectConfigPort = projectConfigPort;
        this.cache = cache;
    }

    /**
     * Retrieves rules for a specific module.
     * @param moduleId Module identifier
     * @return {@link I18nRuleEngine}
     */
    public @NotNull I18nRuleEngine resolve(@NotNull ModuleId moduleId) {
        return cache.computeIfAbsent(moduleId, this::internalResolveCacheMiss);
    }

    /**
     * Invalidates cached rules for a specific module.
     * @param moduleId Module identifier
     */
    public void invalidate(@NotNull ModuleId moduleId) {
        cache.remove(moduleId);
    }

    /**
     * Invalidates all cached module rules.
     */
    public void invalidateAll() {
        cache.clear();
    }

    private @NotNull I18nRuleEngine internalResolveCacheMiss(@NotNull ModuleId moduleId) {
        var projectConfig = projectConfigPort.read();
        var moduleConfig = projectConfig.modules().get(moduleId);

        if (moduleConfig == null) {
            throw new IllegalArgumentException("Unknown module: " + moduleId);
        }

        List<@NotNull EditorRule> rules = moduleConfig.editorRules();
        RuleCompiler compiler = new RuleCompiler();
        CompiledRules compiledRules = compiler.compile(rules);

        return new I18nRuleEngine(compiledRules);
    }
}
