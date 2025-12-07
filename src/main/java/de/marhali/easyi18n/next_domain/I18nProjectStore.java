package de.marhali.easyi18n.next_domain;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project-wide store of all translations.
 *
 * @author marhali
 */
public class I18nProjectStore {

    private final @NotNull MapImplFactory mapImplFactory;

    /**
     * Map including every managed module with their translations.
     */
    private final @NotNull Map<String, I18nModuleStore> byModule;

    public I18nProjectStore(@NotNull MapImplFactory mapImplFactory) {
        this(mapImplFactory, mapImplFactory.get());
    }

    public I18nProjectStore(@NotNull MapImplFactory mapImplFactory, @NotNull Map<String, I18nModuleStore> byModule) {
        this.mapImplFactory = mapImplFactory;
        this.byModule = byModule;
    }

    /**
     * Checks if a specific i18n module exists.
     * @param moduleName Module by name to check
     * @return {@code true} if module exists, otherwise {@code false}
     */
    public boolean hasModule(@NotNull String moduleName) {
        return this.byModule.containsKey(moduleName);
    }

    /**
     * Retrieves a specific i18n module by name.
     * @see #hasModule(String)
     * @param moduleName Module by name to receive
     * @return {@link I18nModuleStore} or {@link NoSuchElementException} if the requested module is unknown
     */
    public @NotNull I18nModuleStore getModule(@NotNull String moduleName) {
        if (!hasModule(moduleName)) {
            throw new NoSuchElementException("Module by name '" + moduleName + "' does not exist");
        }

        return this.byModule.get(moduleName);
    }

    public @NotNull I18nModuleStore getOrCreateModule(@NotNull String moduleName) {
        return this.byModule.computeIfAbsent(
            moduleName,
            (module) -> new I18nModuleStore(new HashSet<>(), this.mapImplFactory.get())
        );
    }

    public @NotNull Set<String> getModuleNames() {
        return this.byModule.keySet();
    }

    /**
     * Retrieves a set of used locales in every module.
     * @return Set of locales
     */
    public Set<String> getAllLocales() {
        return this.byModule.values().stream()
            .flatMap(store -> store.getLocales().stream())
            .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "I18nProjectStore{" +
            "mapImplFactory=" + mapImplFactory +
            ", byModule=" + byModule +
            '}';
    }
}
