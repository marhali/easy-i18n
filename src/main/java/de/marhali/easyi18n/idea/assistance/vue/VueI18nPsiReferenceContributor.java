package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nPsiReferenceContributor;

/**
 * @author marhali
 */
public class VueI18nPsiReferenceContributor extends JavaScriptI18nPsiReferenceContributor {
    public VueI18nPsiReferenceContributor() {
        super(EditorLanguage.VUE);
    }
}
