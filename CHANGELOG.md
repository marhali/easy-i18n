<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# easy-i18n Changelog

## [Unreleased]
### Added
- Regex support for translation file patterns

### Changed
- Reload function internally consolidated

### Fixed
- Parsing for <kbd>.properties</kbd> files

## [4.2.4]
### Changed
- Improved exception handling on syntax errors

### Fixed
- Some settings are not retained on IDE restarts

## [4.2.3]
### Changed
- Removed warning about missing configuration during project initialization

### Fixed
- Folding support for Vue files

## [4.2.2]
### Changed
- Documentation provider better focuses on the actual translation part
- Color duplicate translation values orange to increase contrast in light themes

### Fixed
- Unintended overwrite of existing folding regions

## [4.2.1]
### Changed
- Updated dependencies

### Fixed
- Downgrade java to fix compatibility issues

## [4.2.0]
### Added
- Support for IntelliJ Platform version 2022.2

### Changes
- Updated dependencies

## [4.1.1]
### Fixed
- AssertionError on translation update
- Use correct line separator in <kbd>properties</kbd> files
- Allow numbers and other data types in <kbd>properties</kbd> files

## [4.1.0]
### Added
- Duplicate translation values filter
- Indicate translations with duplicated values yellow
- Multiple translation filters can be used together
- Option to consider subdirectories for modularized translation files
- Reformat translation files based on IDE preferences

### Changed
- Reengineered how translation filters are applied internally

### Fixed
- Exception during batch delete
- Translation filters keep their status across updates

## [4.0.0]
### BREAKING CHANGES
- Configuration rework. Existing settings will be lost and must be configured via the new configuration page

### Added
- Key delimiters (namespace / section) can be configured
- Extract translation intention
- Full language support for Java, Kotlin, JavaScript / TypeScript, Vue and PHP
- Expand already expanded nodes after data update
- Experimental option to force translation key folding
- Individual icon for tool-window and lookup items
- Dedicated configuration file (easy-i18n.xml) inside <kbd>.idea</kbd> folder

### Changed
- Editor assistance has been reengineered. This will affect key suggestion and annotation
- Moved configuration dialog into own page inside <kbd>IDE Settings</kbd> 

### Fixed
- AlreadyDisposedException on FileChangeListener after project dispose
- Request-URL limit for error reports

## [3.2.0]
### Added
- Support for IntelliJ 2022.1

### Changed
- Updated dependencies

## [3.1.0]
### Added
- Support for Json5 files

## [3.0.1]
### Changed
- Fresh projects will receive a notification instead of an exception to configure the plugin

### Fixed
- Exception on json array value mapping

## [3.0.0]
### BREAKING CHANGES
- The local file structure of your translation files must be configured in the settings menu

### Added
- Modularization supports namespace or locale module
- Full namespace / locale module support for all file types
- Support for object and array elements inside json arrays
- IDE integrated error report functionality

### Changed
- Improve exception handling on IO operations
- Update dependencies

### Fixed
- Character unescaping for '.properties' files
- Exception on json files without any content

## [2.0.0]
### BREAKING CHANGES
- Translation file pattern matcher needs to be updated to <kbd>\*.*</kbd> or equivalent wildcard rule 
- I18n key nesting will now escape every delimiter within a section layer (can be inverted via option)

### Added
- Filter functionality for translations with missing values
- Full keyboard shortcut support inside tool-window
- Support for dots within key nodes in YAML files

### Changed
- Improve marking nodes with missing values in tree-view
- Key completion inside editor suggests all keys without any logic
- Translation file pattern uses wildcard matcher instead of regex
- Improve exception handling on IO operations
- Update Qodana to latest version
- Allow tool-window rendering in dumb mode

### Fixed
- First row inside table view is not editable
- Key focus within tree or table view after translation change

## [1.7.1]
### Fixed
- Vue.js template folding support

## [1.7.0]
### Added
- Partial support for translation key folding against actual translation
- Support for json based arb files (flutter)

### Changed
- Updated plugin dependencies
- Use actual file extension to choose io strategy

### Fixed
- NullPointerException on key completion
- Changelog handling in release flow

## [1.6.0]
### Added
- The search function now supports full-text-search
- Automatically reload translation data on file system change
- Sorting of translation keys can now be disabled via configuration
- Key section nesting can be disabled via configuration
- Numbers will be stored as number type whenever possible
- Code signing of plugin source

### Changed
- Better focus keys in tree-view after edit
- Optimized internal data structure (io, cache, events)
- Adjusted compatibility matrix to 2020.3 - 2021.3
- Updated dependencies and improved README file

## [1.5.1]
### Fixed
- Exception on key annotation if path-prefix is undefined

## [1.5.0]
### Added
- Support for YAML locale files. Thanks to @sunarya-thito
- Translation key referencing inside editor  
- Optional path-prefix for translations

### Changed
- Optimized i18n key completion

### Fixed
- Locale file pattern configuration

## [1.4.1]
### Added
- Support for IntelliJ 2021.2

### Changed
- Replace deprecated api methods

### Fixed
- Warning for unset target element inside tree-view toolbar

## [1.4.0]
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