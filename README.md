<div id="top"></div>

<!-- PROJECT SHIELDS -->
[![Build](https://img.shields.io/github/workflow/status/marhali/easy-i18n/Build?style=for-the-badge)](https://github.com/marhali/easy-i18n/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/16316.svg?style=for-the-badge)](https://plugins.jetbrains.com/plugin/16316)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/16316.svg?style=for-the-badge)](https://plugins.jetbrains.com/plugin/16316)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg?style=for-the-badge)](https://paypal.me/marhalide)

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/marhali/easy-i18n">
    <img src="src/main/resources/META-INF/pluginIcon.svg" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Easy I18n</h3>

  <p align="center">
    <a href="https://github.com/marhali/easy-i18n/tree/main/example">Examples</a>
    ·
    <a href="https://github.com/marhali/easy-i18n/issues/new?labels=bug">Report Bug</a>
    ·
    <a href="https://github.com/marhali/easy-i18n/issues/new?labels=enhancement">Request Feature</a>
  </p>
</div>

<!-- Plugin description -->
This is a plugin for easier management of translation files for projects that need to be translated into different languages. Translating large projects has never been so easy with your favorite IDE!

This plugin can be used for any project based on one of the formats and structures listed below.

## Features
- UI Tool-Window which supports _tree-view_ and _table-view_
- Easily **`Add`** / **`Edit`** / **`Delete`** translations
- Filter all translations with _full-text-search_ support
- Editor Assistance: translation intention, completion-contributor, key-annotation and -folding
- Translation key sorting and nesting can be configured
- Extensive configuration options: locales directory, preferred locale, key delimiters
- Missing language translations will be indicated red
- Automatically reloads translation data if any locale file was changed

## Builtin Support
### File Types
**<kbd>JSON</kbd>** - **<kbd>JSON5</kbd>** - **<kbd>YAML</kbd>** - **<kbd>Properties</kbd>**

### Folder Structure
- Single Directory: All translation files are within one directory 
- Modularized (**<kbd>Locale</kbd>** \ **<kbd>Namespace</kbd>**)
- Modularized (**<kbd>Namespace</kbd>** \ **<kbd>Locale</kbd>**)

### Language Support
**<kbd>JavaScript / TypeScript</kbd>** - **<kbd>Vue</kbd>** - **<kbd>Java</kbd>** - **<kbd>Kotlin</kbd>** - **<kbd>PHP</kbd>**

<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "easy-i18n"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/marhali/easy-i18n/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>



<!-- PLUGIN CONFIGURATION -->
## Configuration
- Install plugin. See **_Installation_** section
- Create a directory that will contain all translation files
- Create your individual translation files (e.g. _en.json_, _de.json_). See examples if you need assistance.
- Click on the `Settings` action inside the EasyI18n Tool-Window (<kbd>View</kbd> > <kbd>Tool Windows</kbd> > <kbd>Easy I18n</kbd>)
- Specify locales directory
- Select folder structure and file parser to apply to your translation files
- Translations can now be created / edited or deleted


<!-- USAGE EXAMPLES -->
## Screenshots
![Tree View](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/tree-view.PNG)
![TableView](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/table-view.PNG)
![KeyCompletion](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-completion.PNG)
![KeyAnnotation](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-annotation.PNG)
![KeyEdit](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/key-edit.PNG)
![Settings](https://raw.githubusercontent.com/marhali/easy-i18n/main/example/images/settings.PNG)

_For more examples, please refer to the [Examples Directory](https://github.com/marhali/easy-i18n/tree/main/example)._


<!-- ROADMAP -->
## Roadmap

- [X] JSON5 Support
- [X] Configurable namespace and section separators
- [X] Define default namespace to use if none was provided
- [X] Enhance editor code assistance
- [ ] XML Support
- [ ] Mark duplicate translation values
- [ ] Python language assistance

See the [open issues](https://github.com/marhali/easy-i18n/issues) for a full list of proposed features (and known issues).


<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request





<!-- LICENSE -->
## License

Distributed under the MIT License. See `LICENSE` for more information.





<!-- CONTACT -->
## Contact

Marcel Haßlinger - [@marhali_de](https://twitter.com/marhali_de) - [Portfolio Website](https://marhali.de)

Project Link: [https://github.com/marhali/easy-i18n](https://github.com/marhali/easy-i18n)



<!-- DONATION -->
## Donation
If the project helps you to reduce development time, you can give me a [cup of coffee](https://paypal.me/marhalide) :)

---
Plugin based on the [IntelliJ Platform Plugin Template][template].


<!-- MARKDOWN LINKS & IMAGES -->
[template]: https://github.com/JetBrains/intellij-platform-plugin-template
