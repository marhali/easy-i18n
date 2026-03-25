<template>
  <!-- Call argument (rule-03/04: t() / $t(), argument index 0) -->

  <!-- Resolved keys → folding, hover documentation, and Ctrl+Click reference work -->
  <h1>{{ $t('common:primitive.string.sample') }}</h1>
  <p>{{ $t('user:object.deeply.nested.structure.description') }}</p>

  <!-- Unresolved key → inspection error -->
  <span>{{ $t('common:does.not.exist') }}</span>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

// Resolved key → folding, hover documentation, and Ctrl+Click reference work
const label = t('billing:object.deeply.nested.structure.description');

// Unresolved key → inspection error
const missing = t('billing:does.not.exist');
</script>
