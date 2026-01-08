package de.marhali.easyi18n.next_io;

import de.marhali.easyi18n.next_domain.I18nKey;
import de.marhali.easyi18n.next_domain.I18nParams;
import de.marhali.easyi18n.next_domain.I18nValue;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @param key
 * @param value
 * @param params
 *
 * @author marhali
 */
public record TranslationConsumer(
    @NotNull I18nKey key,
    @NotNull I18nValue value,
    @NotNull I18nParams params
) {
    @Override
    public @NotNull String toString() {
        return "TranslationConsumer{" +
            "key=" + key +
            ", value=" + value +
            ", params=" + params +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TranslationConsumer that = (TranslationConsumer) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value) && Objects.equals(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value, params);
    }
}
