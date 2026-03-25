<?php

// Call argument — Laravel translation helpers (argument index 0)

// --- __() — global translation helper (rule-php-double-underscore) ---

// Resolved keys → folding, hover documentation, and Ctrl+Click reference work
echo __('common:primitive.string.sample');
echo __('user:object.deeply.nested.structure.description');
echo __('billing:object.deeply.nested.structure.description');

// Unresolved key → inspection error
echo __('common:does.not.exist');

// --- trans() — alias helper (rule-php-trans) ---

// Resolved key → folding, hover documentation, and Ctrl+Click reference work
$label = trans('common:object.hybrid\.flat\.structure');

// Unresolved key → inspection error
$label = trans('user:unknown.key');

// --- trans_choice() — pluralisation helper (rule-php-trans-choice) ---

// Resolved key → folding, hover documentation, and Ctrl+Click reference work
$count = 3;
echo trans_choice('billing:primitive.string.sample', $count);

// Unresolved key → inspection error
echo trans_choice('common:does.not.exist', $count);
