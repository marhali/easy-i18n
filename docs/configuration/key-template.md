# Key Template

The key template is used to describe the canonical translation key for every translation.

Therefore, this template must clearly specify how translation keys are determined in a deterministic manner and can later be reconstructed using the extracted I18nParams.

## How it works

At its core, a translation key consists of I18nParams that are concatenated into a string.

During the parsing process, the I18nParams are extracted using the path and file templates and then combined into I18nKeys.

When writing back to the translation files, the I18nKey is broken down again into I18nParams, which are then converted back into translation files using the file and path templates.

## Define a Key Template

In general, the translation key template should reflect the translation keys exactly as they are required by the I18n framework in the respective project or module.

## Examples

### Simple hierarchical key structure

`{fileKey:.}`

`fileKey=[user, account, title]` becomes `user.account.title`

### Namespace key structure

`{pathNamespace}:{fileKey:.}`

`pathNamespace=[common] fileKy=[user, account, title]` becomes `common:user.account.tile`

---

See also [Template Syntax](template-syntax.md).
