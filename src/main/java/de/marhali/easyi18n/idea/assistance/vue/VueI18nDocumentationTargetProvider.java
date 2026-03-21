package de.marhali.easyi18n.idea.assistance.vue;

import de.marhali.easyi18n.core.domain.rules.EditorLanguage;
import de.marhali.easyi18n.idea.assistance.javascript.JavaScriptI18nDocumentationTargetProvider;

/**
 * @author marhali
 */
public class VueI18nDocumentationTargetProvider extends JavaScriptI18nDocumentationTargetProvider {

    public VueI18nDocumentationTargetProvider() {
        super(EditorLanguage.VUE);
    }
}
