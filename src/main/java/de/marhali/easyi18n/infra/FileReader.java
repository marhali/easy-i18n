package de.marhali.easyi18n.infra;

import de.marhali.easyi18n.core.domain.model.*;
import de.marhali.easyi18n.core.domain.template.Templates;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * File reader superclass.
 *
 * @author marhali
 */
public abstract class FileReader {

    protected final @NotNull I18nPath path;
    protected final @NotNull Templates templates;
    protected final @NotNull MutableI18nModule store;

    protected FileReader(@NotNull I18nPath path, @NotNull Templates templates, @NotNull MutableI18nModule store) {
        this.path = path;
        this.templates = templates;
        this.store = store;
    }

    /**
     * Creates a fresh translation producer at root level with path params included.
     * @return {@link TranslationProducer}
     */
    protected @NotNull TranslationProducer createRootProducer() {
        // Root level producer seeded by path params and level zero
        return new TranslationProducer(path.params(), 0);
    }

    /**
     * Produces a translation key by using the given params from the consumer.
     * @param producer Translation producer
     * @return {@link I18nKey}
     */
    protected @NotNull I18nKey produceKey(@NotNull TranslationProducer producer) {
        return templates.key().fromParams(producer.params());
    }

    /**
     * Produces the target translation locale by using the given params from the consumer.
     * @param producer Translation producer
     * @return {@link LocaleId}
     */
    protected @NotNull LocaleId produceLocaleId(@NotNull TranslationProducer producer) {
        var localeIdParamName = I18nBuiltinParam.LOCALE.getParameterName();
        var locales = producer.params().get(localeIdParamName);

        if (locales == null || new HashSet<>(locales).size() != 1) {
            throw new IllegalArgumentException("Producer does not specify exactly one localeId. Instead got: " + locales);
        }

        return new LocaleId(locales.getFirst());
    }

    /**
     * Finally produces the translation and adds it to the store.
     * @param producer Translation producer
     * @param value Translation value
     */
    protected void finallyProduceWithValue(@NotNull TranslationProducer producer, @NotNull I18nValue value) {
        var key = produceKey(producer);
        var localeId = produceLocaleId(producer);
        store.addLocale(localeId);
        store.getOrCreateTranslation(key).put(localeId, value);
    }
}
