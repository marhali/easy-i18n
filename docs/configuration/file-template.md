# File Template

The file template describes the structure of the translation file content.

Therefore, this template must clearly specify how file contents are determined in a deterministic manner and can later be reconstructed using the translations.

## How it works

Translation files typically have a hierarchical structure.
For this reason, the [template syntax](template-syntax.md) has been extended to include definitions for multi-level sub-templates.

Generally, the file template is used to extract I18nParams from the file, which can later be used to generate the canonical translation key.

## Define a File Template

Each level of the translation file needs to be described by the template syntax.

> The last template level also serves as a **fallback** for even higher file levels.

Definition `[rootLevelSyntax][firstLevelSyntax]...[lastAndFallbackLevelSyntax]`

## Examples

### Single file (all in one)

Hierarchical key: `[{locale}][{fileKey}]`

```yaml
de_DE:
  user:
    account:
      title: "Any value"
```

Flat key: `[{locale::[^.]+}{fileKey:.}]`

```yaml
de_DE.user.account.title: "Any value"
```

### Hierarchical / nested file

`[{fileKey}]`

```yaml
user:
  account:
    title: "Any value"
```

### Flat file

`[{fileKey:.}]`

```
user.account.title: "Any value"
```

---

See also [Template Syntax](template-syntax.md).
