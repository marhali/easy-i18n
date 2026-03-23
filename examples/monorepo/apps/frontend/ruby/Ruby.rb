# frozen_string_literal: true

# All examples use call argument (argument index 0)

# --- t() — Rails shorthand helper (rule-ruby-t) ---
# Available in controllers, views, mailers, and helpers

# Resolved keys → folding, hover documentation, and Ctrl+Click reference work
puts t('common:primitive.string.sample')
puts t('user:object.deeply.nested.structure.description')
puts t('billing:object.deeply.nested.structure.description')
puts t('common:object.hybrid\.flat\.structure')

# Unresolved keys → inspection error
puts t('common:does.not.exist')
puts t('user:unknown.key')

# --- translate() — verbose form (rule-ruby-translate) ---

# Resolved key → folding, hover documentation, and Ctrl+Click reference work
label = translate('billing:primitive.string.sample')

# Unresolved key → inspection error
missing = translate('common:does.not.exist')
