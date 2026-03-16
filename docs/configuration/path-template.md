# Path Template

The path template is used to describe the file paths for all translation files.

Therefore, this template must clearly specify how file paths are determined in a deterministic manner and can later be reconstructed using the translations.

## How it works

In the background, the IDE’s [FileTypeIndex](https://plugins.jetbrains.com/docs/intellij/file-based-indexes.html#file-type-index) is used to find all files with the matching file type. 
For this reason, the path template **must** always specify the corresponding file extension at the end (e.g. `.json`).

A regular expression is then constructed from the path template to filter out only the files that actually match.

## Define a Path Template

As a general rule, it’s a good idea to always use the IDE’s [path macros](https://www.jetbrains.com/help/idea/built-in-macros.html). 
For example, `$PROJECT_DIR$` is usually a good place to start for a path template.

Next, the path to the translation files should be mapped within the project.

> Template Placeholders have a default constraint to match until a `/` or `.` occurs (`[^/.]+`).

## Examples

### Single file (all in one)

`$PROJECT_DIR$/locales/translations.json`

- Matches: `$PROJECT_DIR$/locales/translations.json`

### Single directory

`$PROJECT_DIR$/locales/{locale}.json`

- Matches: `$PROJECT_DIR$/locales/de_DE.json`
- Matches: `$PROJECT_DIR$/locales/en_US.json`
- ...

### Namespace

`$PROJECT_DIR$/locales/{pathNamespace}/{locale}.json`

- Matches: `$PROJECT_DIR$/locales/user/de_DE.json`
- Matches: `$PROJECT_DIR$/locales/account/en_GB.json`
- ...

### Additional constraint

`$PROJECT_DIR$/locales/{pathNamespace}/{locale::[a-z]{2}_[A-Z]{2}}.json`

- Matches: `$PROJECT_DIR$/locales/user/de_DE.json`
- No match: `$PROJECT_DIR$/locales/account/index.json`
- ...

---

See also [Template Syntax](template-syntax.md).
