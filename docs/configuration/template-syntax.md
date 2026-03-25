# Template Syntax

The template syntax is the powerful tool behind Easy I18n. It allows the plugin to be used in almost any project.

The template syntax is used for:

- [Path Template](path-template.md)
- [File Template](file-template.md)
- [Key Template](key-template.md)
- [I18n Flavor Template](i18n-flavor-template.md)

## How it works

From a technical standpoint, the template syntax is simply a string that defines how content should be extracted from elements and how to reassemble it later.

To achieve this, the user configures so-called template syntax definitions via the [plugin configuration](index.md).

## Structure

A template syntax definition consists of a sequence of template elements.

### Template Elements

#### Literal

A template literal is literally just a hard-coded string. For example `$PROJECT_DIR$/locales/` would be a template literal if defined in any template syntax definition.

#### Placeholder

A template placeholder defines a dynamic element that will contribute to the I18nParams for parsing & constructing elements.

Definition: `{placeholderName:optionalDelimiter:optionalConstraint}`

Whereas `optionalDelimiter` and `optionalConstraint` are optional and could be omitted.
Thus `{placeholderName}` or `{placeholderName::optionalConstraint}` would also be valid placeholders.

##### `optionalDelimiter`

Optional delimiter string that splits parsed placeholder values.
If the split values contain the delimiter, it will be escaped there then unescaped during reassembly.

##### `optionalConstraint`

Optional constraint string to provide more control. Mostly used to define [regular expressions](https://en.wikipedia.org/wiki/Regular_expression) that are used for parsing.
