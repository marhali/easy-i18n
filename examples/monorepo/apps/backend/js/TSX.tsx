import { useTranslation } from 'react-i18next';

// Call argument (rule-03: t(), argument index 0)
export function Greeting(): JSX.Element {
  const { t } = useTranslation();

  return (
    <div>
      {/* Resolved keys → folding, hover documentation, and Ctrl+Click reference work */}
      <h1>{t('common:primitive.string.sample')}</h1>
      <p>{t('billing:object.deeply.nested.structure.description')}</p>

      {/* Unresolved key → inspection error */}
      <span>{t('user:does.not.exist')}</span>
    </div>
  );
}
