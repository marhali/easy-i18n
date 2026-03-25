# I18n Flavor Template

The I18n flavor template describes how elements in the project that have not yet been translated can be extracted and replaced with a translation using the framework installed in the project.

Basically, the flavor describes how the framework references translations in the project.

## How it works

When applied, the editor replaces the current hard-coded literal with the filled flavor. 
Thus, the flavor should define a placeholder variable named `{i18nKey}` to provide the extracted translation key.

## Examples

### Java with ResourceBundle

`ResourceBundle.getBundle("myBundle").getString("{i18nKey}")`
