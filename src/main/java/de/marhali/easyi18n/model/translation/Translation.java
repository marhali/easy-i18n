package de.marhali.easyi18n.model.translation;

import de.marhali.easyi18n.model.translation.variant.Plural;

import de.marhali.easyi18n.model.translation.variant.ContextMap;
import de.marhali.easyi18n.model.translation.variant.LocaleMap;
import de.marhali.easyi18n.model.translation.variant.PluralMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the set values behind a specific translation.
 * Consideration is given to context, pluralization and locale.
 * <br />
 * Data structure can be imagined like a layered map of: context => plural => locale
 *
 * @author marhali
 */
public class Translation {

    private @Nullable String description;
    private @NotNull ContextMap contexts;
    private @Nullable Object misc;

    public Translation(@Nullable String description, @NotNull ContextMap contexts, @Nullable Object misc) {
        this.description = description;
        this.contexts = contexts;
        this.misc = misc;
    }

    public Translation() {
        this(null, new ContextMap(), null);
    }

    /**
     * Retrieve additional description for this translation
     * @return Description
     */
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Override or set description for this translation
     * @param description Description
     */
    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    /**
     * Retrieve all contexts for this translation
     * @return Map of specified contexts
     */
    public @NotNull ContextMap getContexts() {
        return contexts;
    }

    /**
     * Check whether a specific context has been set for this translation
     * @param context Context to check
     * @return True if context has been configured otherwise false
     */
    public boolean hasContext(@NotNull String context) {
        return contexts.containsKey(context);
    }

    /**
     * Retrieve all plurals for a specific context.
     * @param context Context to apply
     * @return Map of specified plurals
     */
    public @Nullable PluralMap getPlurals(@NotNull String context) {
        return contexts.get(context);
    }

    /**
     * Retrieve all locale translations for a specific context & pluralization
     * @param context Context to apply
     * @param plural Pluralization to apply
     * @return Map of specified locales
     */
    public @Nullable LocaleMap getLocales(@NotNull String context, @NotNull Plural plural) {
        return contexts.getOrDefault(context, new PluralMap()).get(plural);
    }

    /**
     * Retrieve a specific locale translation for a specific context, pluralization and locale
     * @param context Context to apply
     * @param plural Pluralization to apply
     * @param locale Locale to apply
     * @return Translated locale value for the specified variant
     */
   public @Nullable String getValue(@NotNull String context, @NotNull Plural plural, @NotNull String locale) {
        return contexts.getOrDefault(context, new PluralMap()).getOrDefault(plural, new LocaleMap()).get(locale);
   }

    /**
     * Override or set context map.
     * @param contexts New contexts
     */
   public void set(@NotNull ContextMap contexts) {
       this.contexts = contexts;
   }

    /**
     * Override or set a specific context
     * @param context Context to use
     * @param plurals New plurals map
     */
   public void set(@NotNull String context, @NotNull PluralMap plurals) {
       contexts.put(context, plurals);
   }

    /**
     * Override or set locales for a specific context & pluralization
     * @param context Context to use
     * @param plural Pluralization to use
     * @param locales New locales map
     */
   public void set(@NotNull String context, @NotNull Plural plural, @NotNull LocaleMap locales) {
        PluralMap plurals = getPlurals(context);

        if(plurals == null) {
            plurals = new PluralMap();
        }

        plurals.put(plural, locales);
        set(context, plurals);
   }

    /**
     * Override or update a specific translation variant
     * @param context Context to use
     * @param plural Pluralization to use
     * @param locale Locale to use
     * @param value New value to set
     */
   public void set(@NotNull String context, @NotNull Plural plural, @NotNull String locale, @NotNull String value) {
        LocaleMap locales = getLocales(context, plural);

        if(locales == null) {
            locales = new LocaleMap();
        }

       locales.put(locale, value);
        set(context, plural, locales);
   }

    /**
     * I18n support data
     * @return Data
     */
    public @Nullable Object getMisc() {
        return misc;
    }

    /**
     * Set or update I18n support data
     * @param misc New Data
     */
    public void setMisc(@Nullable Object misc) {
        this.misc = misc;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "description='" + description + '\'' +
                ", contexts=" + contexts +
                ", misc=" + misc +
                '}';
    }
}
