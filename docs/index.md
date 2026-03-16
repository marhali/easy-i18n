# Welcome

Easy I18n is a plugin based on the [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html) to handle the process of [internationalization](https://en.wikipedia.org/wiki/Internationalization_and_localization) in your project.

> **Translating large projects using your favorite IDE has never been easier!**

This plugin offers a wide range of [configuration options](configuration/index.md) to adapt to the specific requirements of the project.
However, to help you get started quickly, there are a variety of [presets](configuration/presets.md) for common use cases.

___

## Features

- Support for Multi-Module projects, ideally for monorepos
- [Translations Tool Window](components/tool-window.md) to manage all your translations in a single place
    - Visualize as _tree_ or _table_ view
    - Filter by _full-text-search_ query
    - Filter and highlighting of _duplicate_ or _missing_ translation values
- Easily **Add** / **Edit** or **Delete** translations via the [Translation Dialog](components/dialog.md) or [Tool Window](components/tool-window.md)
- Configuring of translation sources using a powerful [template syntax](configuration/template-syntax.md)
- Fine-grained editor assistance using user-defined [rules](configuration/editor-rules.md)
    - Referencing of translation keys to quickly jump the [Translation Dialog](components/dialog.md)
    - Inspection to find unresolved translation keys
    - Quickfix intention action to add translations
    - Extract translation action to localize hard-coded literals
    - Documentation provider to preview translation values
    - Folding of translation keys with preview locale value

___

## Builtin Support

### File Types

`JSON` - `JSON5` - `YAML` - `Properties`

### Editor Language Assistance

`JavaScript` - `TypeScript` - `JSX` - `TSX` - `Vue` - `Java` - `Kotlin` - `PHP`
