# easy-i18n

![Build](https://github.com/marhali/easy-i18n/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

## Template ToDo list
- [x] Create a new [IntelliJ Platform Plugin Template][template] project.
- [ ] Verify the [pluginGroup](/gradle.properties), [plugin ID](/src/main/resources/META-INF/plugin.xml) and [sources package](/src/main/kotlin).
- [ ] Review the [Legal Agreements](https://plugins.jetbrains.com/docs/marketplace/legal-agreements.html).
- [ ] [Publish a plugin manually](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html?from=IJPluginTemplate) for the first time.
- [ ] Set the Plugin ID in the above README badges.
- [ ] Set the [Deployment Token](https://plugins.jetbrains.com/docs/marketplace/plugin-upload.html).
- [ ] Click the <kbd>Watch</kbd> button on the top of the [IntelliJ Platform Plugin Template][template] to be notified about releases containing new features and fixes.

<!-- Plugin description -->
This is an easy plugin to manage internationalization for JSON or Resource-Bundle(Properties) based locale files.
Most common use case is for translating Webapps or simple Java Applications.

![Tree View](https://github.com/marhali/easy-i18n/tree/example/images/TreeView.PNG "Tree View")
![Table View](https://github.com/marhali/easy-i18n/tree/example/images/TableView.PNG "Table View")

## Use Cases
- Webapps: [Vue](https://vuejs.org/) with [vue-i18n](https://kazupon.github.io/vue-i18n/) or any other JSON translation file based technology
- Java based Resource-Bundle

## Features
- UI Tool Window with Table- and Tree-View representation
- Easily Add / Edit / Delete translations
- Filter / Search function to hide irrelevant keys
- Configurable locales directory & preferred locale for ui presentation 
- Translation keys with missing definition for any locale will be displayed red
- Quick edit any translation by right-click (IntelliJ Popup Action)
- Quick delete any translation via <kbd>DEL</kbd>-Key

## Configuration
- Install plugin. See **Installation** section
- Create a directory which will hold the locale files
- Create a file for each required locale (e.g de.json, en.json) Note: Each json file must at least define an empty section (e.g. **{}**)
- Click on the **Settings** Action inside the Easy I18n Tool Window
- Select the created directory (optional: define the preferred locale to view) and press Ok
- Translations can now be created / edited or deleted
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "easy-i18n"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/marhali/easy-i18n/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
