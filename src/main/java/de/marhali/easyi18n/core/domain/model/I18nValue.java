package de.marhali.easyi18n.core.domain.model;

import de.marhali.easyi18n.core.domain.misc.I18nValueEscaper;
import org.jetbrains.annotations.NotNull;

/**
 * Single localized translation value.
 * The value might or should be formatted as the configured file codec expects these values.
 *
 * @param raw Raw translation value which should be escaped
 */
public record I18nValue(
    @NotNull String raw
) {

    /**
     * Shorthand to construct a translation value from an already escaped value.
     * @param value Escaped translation value
     * @return {@link I18nValue}
     */
    public static @NotNull I18nValue fromEscaped(@NotNull String value) {
        return new I18nValue(value);
    }

    /**
     * Shorthand to construct a translation value from a unescaped value.
     * @param value Unescaped translation value
     * @return {@link I18nValue}
     */
    public static @NotNull I18nValue fromUnescaped(@NotNull String value) {
        return new I18nValue(I18nValueEscaper.escape(value));
    }

    /**
     * Constructs a translation value from a user given input string.
     * @param input Input string
     * @return {@link I18nValue}
     */
    public static @NotNull I18nValue fromInputString(@NotNull String input) {
        return new I18nValue(input);
    }

    /**
     * Returns this translation value for a user presented input string
     * @return {@link String}
     */
    public @NotNull String toInputString() {
        return raw;
    }

    /**
     * Returns this translation value with unescaped control characters.
     * @return {@link String}
     */
    public @NotNull String toUnescaped() {
        return I18nValueEscaper.unescape(raw);
    }
}
