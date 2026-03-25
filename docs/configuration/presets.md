# Presets

Presets are predefined configuration settings that make getting started much easier. They can be applied at the module level.

The following presets are currently available:

## Module Presets

### Default

A blank starting point with all fields empty. Use this when you want to configure everything manually.

- **File format**: JSON
- **Editor rules**: none

---

### Custom

Preserves the current module configuration unchanged.
This preset is automatically selected if you customize your configuration.

---

## Framework-agnostic Presets

### Rails

Ruby on Rails with the standard [I18n gem](https://guides.rubyonrails.org/i18n.html).

- **File format**: YAML
- **File layout**: `config/locales/{locale}.yml` — locale is the root key inside the file
- **Key format**: dot-separated (`users.name`)
- **Flavor template**: `I18n.t('{i18nKey}')`
- **Editor rules**:
  - `t('key')` — shorthand helper in controllers, views, and mailers
  - `translate('key')` — verbose form

---

### Vue I18n

[Vue I18n v9](https://vue-i18n.intlify.dev/) (Composition API and Options API).

- **File format**: JSON
- **File layout**: `src/locales/{locale}.json`
- **Key format**: dot-separated (`users.name`)
- **Flavor template**: `t('{i18nKey}')`
- **Editor rules**:
  - `t('key')` — Composition API `useI18n` hook (JavaScript, TypeScript, Vue)
  - `$t('key')` — Options API and template inline expressions (JavaScript, TypeScript, Vue)

---

### React i18next

[react-i18next](https://react.i18next.com/) and [next-i18next](https://github.com/i18next/next-i18next) with namespace layout.

- **File format**: JSON
- **File layout**: `public/locales/{locale}/{namespace}.json`
- **Key format**: `namespace:subKey` (e.g. `common:greeting`)
- **Flavor template**: `t('{i18nKey}')`
- **Editor rules**:
  - `t('key')` — `useTranslation` hook (JavaScript, TypeScript)

---

### Spring Boot

[Spring Boot MessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html) and Java `ResourceBundle`.

- **File format**: Properties
- **File layout**: `src/main/resources/messages_{locale}.properties`
- **Key format**: dot-separated (`users.name`)
- **Flavor template**: `messageSource.getMessage("{i18nKey}", null, locale)`
- **Editor rules**:
  - `messageSource.getMessage('key', ...)` — Spring `MessageSource` (Java, Kotlin)
  - `bundle.getString('key')` — standard Java `ResourceBundle` (Java, Kotlin)

---

### Laravel

[Laravel](https://laravel.com/docs/localization) JSON translation strings (Laravel 5.4+).

- **File format**: JSON
- **File layout**: `resources/lang/{locale}.json`
- **Key format**: dot-separated (`users.name`)
- **Flavor template**: `__('{i18nKey}')`
- **Editor rules**:
  - `__('key')` — global translation helper (PHP)
  - `trans('key')` — alias helper (PHP)
  - `trans_choice('key', n)` — pluralisation helper, first argument (PHP)

---

### Angular ngx-translate

[ngx-translate](https://github.com/ngx-translate/core) for Angular.

- **File format**: JSON
- **File layout**: `src/assets/i18n/{locale}.json`
- **Key format**: dot-separated (`users.name`)
- **Flavor template**: `this.translate.instant("{i18nKey}")`
- **Editor rules**:
  - `translate.instant('key')` — synchronous lookup (TypeScript)
  - `translate.get('key')` — observable lookup (TypeScript)

---

Is there a preset missing, or does a configuration not quite fit the framework? [Let me know on GitHub!](https://github.com/marhali/easy-i18n)
