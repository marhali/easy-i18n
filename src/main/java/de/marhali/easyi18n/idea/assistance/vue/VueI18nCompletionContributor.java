package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nCompletionContributor;

/**
 * @author marhali
 */
public class VueI18nCompletionContributor extends JavaScriptI18nCompletionContributor {
    public VueI18nCompletionContributor() {
        super(EditorLanguage.VUE);
    }
}
