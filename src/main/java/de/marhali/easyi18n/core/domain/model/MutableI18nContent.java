package de.marhali.easyi18n.core.domain.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Content of a translation.
 *
 * @author marhali
 * @see I18nContent
 */
public final class MutableI18nContent {

    public static @NotNull MutableI18nContent fromSnapshot(@NotNull I18nContent content) {
        return new MutableI18nContent(new HashMap<>(content.values()), content.comment());
    }

    /**
     * Translation values mapped by localeId identifier.
     */
    private final @NotNull Map<@NotNull LocaleId, @NotNull I18nValue> values;

    /**
     * Optional description.
     */
    private @Nullable String comment;

    public MutableI18nContent() {
        this(new HashMap<>(), null);
    }

    public MutableI18nContent(
        @NotNull Map<@NotNull LocaleId, @NotNull I18nValue> values,
        @Nullable String comment
    ) {
        this.values = values;
        this.comment = comment;
    }

    /**
     * @return A list of localeId identifiers that contain translated values.
     */
    public @NotNull Set<LocaleId> getLocales() {
        return values.keySet();
    }

    /**
     * Checks whether a value for the specified localeId is present or not.
     *
     * @param localeId Locale identifier
     * @return {@code true} if localeId is translated, otherwise {@code false}.
     */
    public boolean has(@NotNull LocaleId localeId) {
        return values.containsKey(localeId);
    }

    /**
     * Inserts or updates a translated value
     *
     * @param localeId Locale identifier
     * @param value    New translated value
     */
    public void put(@NotNull LocaleId localeId, @NotNull I18nValue value) {
        values.put(localeId, value);
    }

    /**
     * Removes a translated value for the specified localeId.
     *
     * @param localeId Locale identifier
     */
    public void remove(@NotNull LocaleId localeId) {
        values.remove(localeId);
    }

    /**
     * Checks whether this translation content has a description or not.
     *
     * @return {@code true} if a comment is present, otherwise {@code false}.
     */
    public boolean hasComment() {
        return comment != null;
    }

    /**
     * Updates the description on this translation content.
     *
     * @param comment New translation description. Can be {@code null}.
     */
    public void setComment(@Nullable String comment) {
        this.comment = comment;
    }

    public @NotNull I18nContent toSnapshot() {
        return new I18nContent(new HashMap<>(values), comment);
    }

    @Override
    public String toString() {
        return "I18nContent{" +
            "values=" + values +
            ", comment='" + comment + '\'' +
            '}';
    }
}
