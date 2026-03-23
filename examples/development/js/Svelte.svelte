<script>
  import { t } from 'svelte-i18n';

  // Call argument (rule-03: t(), argument index 0)

  // Resolved key → folding, hover documentation, and Ctrl+Click reference work
  const label = $t('common:primitive.string.sample');

  // Unresolved key → inspection error
  const missing = $t('common:does.not.exist');
</script>

<!-- Resolved keys → folding, hover documentation, and Ctrl+Click reference work -->
<h1>{$t('user:object.deeply.nested.structure.description')}</h1>
<p>{$t('billing:object.deeply.nested.structure.description')}</p>

<!-- Unresolved key → inspection error -->
<span>{$t('common:does.not.exist')}</span>
