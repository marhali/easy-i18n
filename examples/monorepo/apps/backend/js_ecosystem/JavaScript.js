import { useTranslation } from 'react-i18next';

const { t } = useTranslation();

// --- Call argument (rule-03: t(), argument index 0) ---

// Resolved key → folding, hover documentation, and Ctrl+Click reference work
const greeting = t('common:primitive.string.sample');
const description = t('common:object.deeply.nested.structure.description');

// Unresolved key → inspection error
const broken = t('common:does.not.exist');

// --- Declaration target (rule-02: variable named "i18nKey") ---

// Resolved key → folding, hover documentation, and Ctrl+Click reference work
const i18nKey = 'user:object.deeply.nested.structure.description';

// Unresolved key → inspection error
const i18nKey2 = 'billing:unknown.key';
