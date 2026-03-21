package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nFoldingBuilder;

/**
 * @author marhali
 */
public class VueI18nFoldingBuilder extends JavaScriptI18nFoldingBuilder {
    public VueI18nFoldingBuilder() {
        super(EditorLanguage.VUE);
    }
}
