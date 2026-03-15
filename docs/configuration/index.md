# Overview

The plugin is configured via project-specific configuration options.
The configuration is stored inside the project under `.idea/easy-i18n.xml`.

## Options

### Common Configuration

Common options are applied to all modules within the project.

#### Sort translation key alphabetically
- **Description**: Indicates whether translation keys should be sorted alphabetically or not.
  If disabled, the order as in the translation files is kept.
- **Recommendation**: `true`

#### Preview locale
- **Description**: Locale to use for in-editor translation previews.
- **Recommendation**: _Your native language_

### Modules Configuration

A module represents a self-contained component of the project that supports translations (e.g. backend, frontend, ...).

_Resource Configuration_

#### Path template
- **Description**: Template syntax to match and construct relevant translation file paths.
- **Example**: `$PROJECT_DIR$/apps/frontend/locales/{locale}.json`
- See also [Path Template](path-template.md).

#### File template
- **Description**: Template syntax to parse and construct translation file contents and selection of the translation file content type.
- **Example**: `[{fileKey}]``
- See also [File Template](file-template.md).

#### Key template
- **Description**: Template syntax to parse and construct translation keys.
- **Example**: `{pathNamespace}:{fileKey:.}`
- See also [Key Template](key-template.md).

#### Root directory
- **Description**: Root directory for which the module is responsible for translations. Usually used to control editor assistance.
- **Recommendation**: _Source directory of your module_
- **Example**: `$PROJECT_DIR$/apps/frontend/src`

_Editor Configuration_

#### Default key prefixes
- **Description**: Translation key prefixes to prepend on translation keys found inside the editor. Might be useful for e.g. default namespaces.
- **Example**: `common:`

#### I18n flavor template
- **Description**: Replacement string to apply if a hard-coded literal is extracted to a translation.
- **Example**: `ResourceBundle.getBundle("xyz").getString("{i18nKey}")`

#### Key assistance rule
- **Description**: Rules for matching specific editor elements to enable in-editor assistance.
- See also [Editor Rules](editor-rules.md).
