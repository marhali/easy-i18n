from gettext import gettext
from flask_babel import lazy_gettext

# All examples use call argument (argument index 0)

# --- _() — Django / standard gettext shorthand (rule-python-underscore) ---

_ = gettext

# Resolved keys → folding, hover documentation, and Ctrl+Click reference work
print(_('common:primitive.string.sample'))
print(_('user:object.deeply.nested.structure.description'))

# Unresolved key → inspection error
print(_('common:does.not.exist'))


# --- gettext() — explicit call (rule-python-gettext) ---

# Resolved keys → folding, hover documentation, and Ctrl+Click reference work
label = gettext('billing:object.deeply.nested.structure.description')
title = gettext('common:object.hybrid\.flat\.structure')

# Unresolved key → inspection error
missing = gettext('user:unknown.key')


# --- lazy_gettext() — Flask-Babel deferred translation (rule-python-lazy-gettext) ---

# Resolved key → folding, hover documentation, and Ctrl+Click reference work
GREETING = lazy_gettext('billing:primitive.string.sample')

# Unresolved key → inspection error
MISSING = lazy_gettext('common:does.not.exist')
