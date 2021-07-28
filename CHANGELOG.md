<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# easy-i18n Changelog

## [Unreleased]
## [1.4.1]
### Added
- Support for IntelliJ 2021.2

### Changed
- Replace deprecated api methods

### Fixed
- Warning for unset target element inside tree-view toolbar

## [1.4.0]
### THANKS FOR OVER 1000 DOWNLOADS SO FAR!

### Added
- Basic support for json array values
- Settings option to opt-out code assistance inside editor
- Support key completion and annotation for Kotlin language
- Example locale files for all configuration options
- Donation links on GitHub to support development

### Changed
- Update dependencies
- Migrate gradle build script

## [1.3.0]
### Added
- Scroll to created / edited translation inside Tree-/Table-View
- Support for working with multiple projects at once

### Changed
- Update dependencies
- Load translations even if ui tool window is not opened

### Fixed
- NullPointerException's on translation annotation / completion inside editor
- Always synchronize ui with loaded state by reloadFromDisk function

## [1.2.0]
### Added
- Sorting for properties files

### Fixed
- Unexpected character escaping for json/properties files / issue #10

## [1.1.1]
### Added
- Support for IntelliJ 2021.1

### Changed
- Update dependencies

### Fixed
- Exception during i18n key completion / annotation

## [1.1.0] - 2021-04-25
### Added
- Filter option for translation files via regex / issue #5
- Support for splitted / modularized json files per locale / issue #4
- Basic translation key completion inside editor
- I18n key annotation inside editor

### Changed
- Tree view will be expanded if search function has been used

### Fixed
- Encoding for properties files / issue #6

## [1.0.1] - 2021-03-16
### Changed
- Modified plugin icon to meet IntelliJ guidelines

## [1.0.0] - 2021-03-15 (release)
### Added
- Support for JSON and Resource-Bundle(Properties) based locale files
- UI Tool Window with Table- and Tree-View representation
- Add / Edit / Delete functions
- Search function to filter visible keys  
- Quick edit via right-click on any key
- Quick edit via DEL-Key on any key  
- Locales directory / preferred locale can be configured via Settings Dialog
- I18n keys with one or more missing locale translation will be show as red
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

### Changed
- README.md