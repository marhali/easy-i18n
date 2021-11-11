# easy-i18n

![Build](https://github.com/marhali/easy-i18n/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/16316.svg)](https://plugins.jetbrains.com/plugin/16316)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16316.svg)](https://plugins.jetbrains.com/plugin/16316)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://paypal.me/marhalide)

<!-- Plugin description -->
This is a plugin for easier management of translation files of projects that need to be translated into different languages. Translating large projects has never been so easy with your favorite IDE!

## Use Cases
- Webapps: [Vue](https://vuejs.org/) with [vue-i18n](https://kazupon.github.io/vue-i18n/), [React](https://reactjs.org/) or any other json based technology
- Java projects based on Resource-Bundle's
- Projects that uses yaml, json or properties as locale file base for internationalization

## Features
- UI Tool Window which supports tree- or table-view
- Easily Add / Edit / Delete translations
- Filter function with full-text-search support
- Editor Assistance: Key completion, annotation and referencing
- Key sorting and nesting can be configured
- Configurable locales directory & preferred locale for ui presentation
- Missing language translations will be indicated red
- Quick actions: <kbd>right-click</kbd> or <kbd>DEL</kbd> to edit or delete a translation
- Automatically reloads translation data if any locale file was changed
<!-- Plugin description end -->

## Screenshots
![Tree View](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/tree-view.PNG)
![TableView](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/table-view.PNG)
![KeyCompletion](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-completion.PNG)
![KeyAnnotation](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-annotation.PNG)
![KeyEdit](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-edit.PNG)
![Settings](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/settings.PNG)

## Supported IO Strategies (locale files)
- Json: <kbd>json</kbd> files inside locales directory
- Namespaced Json: Multiple <kbd>json</kbd> files per locale directory
- Yaml: <kbd>yml</kbd> or <kbd>yaml</kbd> files inside locales directory
- Properties: <kbd>properties</kbd> files inside locales directory

If there are any files in the locales folder that should not be processed, they can be ignored with the <kbd>Translation file pattern</kbd> option.

## Installation
- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "easy-i18n"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/marhali/easy-i18n/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## Configuration
- Install plugin. See **Installation** section
- Create a directory which will hold the locale files
- Create a file for each required locale (e.g de.json, en.json) Note: Each json file must at least define an empty section (e.g. **{}**)
- Click on the **Settings** Action inside the EasyI18n Tool Window
- Select the created directory (optional: define the preferred locale to view) and press **Ok**
- Translations can now be created / edited or deleted

Examples for the configuration can be found in the [/example](https://github.com/marhali/easy-i18n/tree/main/example) folder.

## Donation
If the project helps you to reduce development time, you can give me a [cup of coffee](https://paypal.me/marhalide) :) 

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
