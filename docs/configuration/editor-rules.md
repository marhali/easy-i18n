# Editor Rules

Using editor rules, you can specify which string literals inside the code editor should receive translation assistance (completion, folding, references, inspections, hover documentation, and extract intention).

This is typically used to target the framework-specific helper methods responsible for translations in your project — for example `I18n.t(...)` in Rails, `t(...)` in Vue/React i18n, or `ResourceBundle.getString(...)` in Java.

## How Matching Works

For each module, you can configure any number of editor rules. If no rules are defined, no assistance is applied.

When the cursor lands on a string literal, the extractor for that language builds an **`EditorElement`** snapshot that captures the structural context of the literal (which method was called, what variable it is assigned to, etc.). The rule engine then evaluates all rules against this snapshot.

**Evaluation order:**

1. Rules are sorted by `priority` descending (highest first).
2. Candidate rules are narrowed to those whose `language` and `trigger` match the element.
   - Rules with an **empty language set** are global fallbacks and apply to every language.
   - Rules whose `trigger` is `UNKNOWN` match every trigger kind.
3. Constraints are evaluated in order. All constraints of a rule must pass for the rule to match.
4. The **first** matching rule with `exclude = false` activates assistance.
5. A matching rule with `exclude = true` **immediately blocks** assistance, regardless of any other rules.

---

## Editor Rule

### Id
- **Description**: A unique name for this rule (used for display only).
- **Type**: String
- **Example**: `rule-java-ResourceBundle`

### Language
- **Description**: Set of programming languages this rule applies to. Leave empty to apply the rule to all languages.
- **Type**: Set of [`EditorLanguage`](../../src/main/java/de/marhali/easyi18n/core/domain/rules/EditorLanguage.java)
- **Supported values**: `JAVA`, `KOTLIN`, `JAVASCRIPT`, `TYPESCRIPT`, `PHP`, `XML`, `HTML`, `VUE`, `GO`, `DART`, `RUST`, `PYTHON`, `SVELTE`, `RUBY`

### Trigger
- **Description**: Syntactic position of the string literal that activates the rule.
- **Type**: [`TriggerKind`](../../src/main/java/de/marhali/easyi18n/core/domain/rules/TriggerKind.java)
- **Options**:

| Value                | Description                                                              | Examples                                          |
|----------------------|--------------------------------------------------------------------------|---------------------------------------------------|
| `CALL_ARGUMENT`      | The literal is an argument to a function or method call.                 | `t("key")`, `I18n.t("key")`, `getString("key")`   |
| `DECLARATION_TARGET` | The literal is the right-hand side of a variable or field assignment.    | `String k = "key"`, `key = 'key'`, `val k = "key"` |
| `RETURN_VALUE`       | The literal is directly returned from a function or method.              | `return "key"`                                    |
| `PROPERTY_VALUE`     | The literal is the value of a hash/object entry or annotation attribute. | `{ key: "value" }`, `@Ann(attr = "key")`          |
| `UNKNOWN`            | Matches any trigger. Use this to write a catch-all rule.                 | -                                                 |

### Priority
- **Description**: Evaluation order within the sorted candidate list. Higher values are evaluated first. When two rules could both match, the one with the higher priority wins.
- **Type**: Integer (default `0`)
- **Example**: `100`

### Exclude
- **Description**: When `true`, a matching rule **blocks** assistance instead of enabling it. Evaluated before any include rule — the moment an exclude rule matches, processing stops and no assistance is provided.
- **Type**: Boolean (default `false`)
- **Use case**: Exclude specific method calls or files from assistance without removing a broader include rule.

---

## Editor Rule Constraints

Each rule can have any number of constraints. **All constraints must pass** for the rule to match. Constraints can be negated individually.

### Type
- **Description**: Which piece of context to inspect.
- **Type**: [`RuleConstraintType`](../../src/main/java/de/marhali/easyi18n/core/domain/rules/RuleConstraintType.java)

#### Context constraints

| Type              | Inspects                                                                                                                                                        | Available for trigger |
|-------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------|
| `LANGUAGE`        | The programming language name (e.g. `JAVA`, `RUBY`). Useful in global fallback rules to narrow the language match.                                              | all                   |
| `LITERAL_KIND`    | How the string is written in source. See [Literal Kind](#literal-kind) values below.                                                                            | all                   |
| `STATIC_ONLY`     | Whether the string value is statically known at rule-evaluation time (`true` / `false`). Normally always `true` for non-interpolated strings.                   | all                   |
| `FILE_PATH`       | Absolute path of the source file.                                                                                                                               | all                   |
| `IN_TEST_SOURCES` | Whether the file resides inside a test source root (`true` / `false`).                                                                                          | all                   |
| `IMPORT_SOURCE`   | Checks whether **any** import/require statement in the file matches. Matches against the imported identifier or path (e.g. `java.util.ResourceBundle`, `i18n`). | all                   |
| `TEXT_PATTERN`    | Matches against the literal string value itself. Useful to only activate assistance for keys that follow a naming convention.                                   | all                   |

#### Call argument constraints (`CALL_ARGUMENT` trigger)

| Type                | Inspects                                                                                                                                                                |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CALLABLE_NAME`     | Name of the called method or function (e.g. `t`, `getString`, `translate`).                                                                                             |
| `CALLABLE_FQN`      | Fully-qualified name of the callable, including the class (e.g. `java.util.ResourceBundle.getString`). Available when the language extractor can resolve the reference. |
| `RECEIVER_TYPE_FQN` | Fully-qualified type of the receiver object in a method-call chain (e.g. `java.util.ResourceBundle`).                                                                   |
| `ARGUMENT_INDEX`    | Zero-based index of the argument position (e.g. `0` for the first argument).                                                                                            |
| `ARGUMENT_NAME`     | Name of the formal parameter at that argument position. Available when the language extractor can resolve the method signature.                                         |

#### Declaration constraints (`DECLARATION_TARGET` trigger)

| Type                 | Inspects                                                                                                                                                             |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DECLARATION_NAME`   | Name of the variable or field being assigned (e.g. `messageKey`, `translationKey`).                                                                                  |
| `DECLARATION_MARKER` | Annotations or decorators on the variable, field, or enclosing method parameter. Matches against any one of them (e.g. `org.springframework.context.MessageSource`). |

#### Return value constraints (`RETURN_VALUE` trigger)

| Type                 | Inspects                                             |
|----------------------|------------------------------------------------------|
| `CALLABLE_NAME`      | Name of the method from which the value is returned. |
| `CALLABLE_FQN`       | Fully-qualified name of the enclosing method.        |
| `DECLARATION_MARKER` | Annotations or decorators on the enclosing method.   |

#### Property constraints (`PROPERTY_VALUE` trigger)

| Type            | Inspects                                                                                        |
|-----------------|-------------------------------------------------------------------------------------------------|
| `PROPERTY_NAME` | Name of the property or hash key (e.g. `label`, `message`).                                     |
| `PROPERTY_PATH` | Full property path when nested structures are supported. Currently the same as `PROPERTY_NAME`. |

#### Exclusion shorthand

| Type      | Inspects                                                                                                      |
|-----------|---------------------------------------------------------------------------------------------------------------|
| `EXCLUDE` | Same as `TEXT_PATTERN` — matches the literal value. Intended to be used in exclude rules as a readable alias. |

---

### Value
- **Description**: The string to compare against. Interpretation depends on the selected `Match Mode`.
- **Type**: String
- **Example**: `java.util.ResourceBundle.getString`

### Match Mode
- **Description**: Algorithm used to compare the constraint value against the actual value.
- **Type**: [`TextMatchMode`](../../src/main/java/de/marhali/easyi18n/core/domain/rules/TextMatchMode.java)

| Mode       | Behaviour                                                                                                |
|------------|----------------------------------------------------------------------------------------------------------|
| `EXACT`    | The actual value must equal the constraint value exactly.                                                |
| `PREFIX`   | The actual value must start with the constraint value.                                                   |
| `SUFFIX`   | The actual value must end with the constraint value.                                                     |
| `CONTAINS` | The actual value must contain the constraint value as a substring.                                       |
| `REGEX`    | The actual value must fully match the constraint value as a Java regular expression (`Pattern.matches`). |

### Negated
- **Description**: When `true`, the constraint passes only if the match **fails**. Combine with `Exclude` on the rule to exclude specific sub-cases within a broader rule.
- **Type**: Boolean (default `false`)

---

## Literal Kind

The `LITERAL_KIND` constraint compares against the syntactic form of the string literal extracted by the language plugin.

| Value                         | Description                                                                                                                                | Languages                           |
|-------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------|
| `STRING`                      | A regular quoted string literal (`"..."` or `'...'`).                                                                                      | All                                 |
| `TEXT_BLOCK`                  | A multi-line block string (`"""..."""` in Java/Python, triple-quoted strings).                                                             | Java, Python, Dart                  |
| `TEMPLATE_NO_INTERPOLATION`   | A template literal without any interpolated expressions (`` `...` `` in JS/TS).                                                            | JavaScript, TypeScript, Vue, Svelte |
| `TEMPLATE_WITH_INTERPOLATION` | A template literal that contains at least one interpolated expression (`` `${expr}` ``). These are excluded from i18n matching by default. | JavaScript, TypeScript, Vue, Svelte |
| `HEREDOC`                     | A heredoc string (`<<~HEREDOC` in Ruby, `<<<EOT` in PHP).                                                                                  | Ruby, PHP                           |
| `NOWDOC`                      | A non-interpolating heredoc (`<<<'EOT'` in PHP).                                                                                           | PHP                                 |
| `UNKNOWN`                     | Literal kind could not be determined.                                                                                                      | —                                   |

---

## Examples

### Match any call to `t()` in Ruby files

```json
{
  "id": "rule-ruby-i18n-t",
  "language": ["RUBY"],
  "trigger": "CALL_ARGUMENT",
  "priority": 10,
  "exclude": false,
  "constraints": [
    { "type": "CALLABLE_NAME", "value": "t", "matchMode": "EXACT", "negated": false }
  ]
}
```

### Match `I18n.t()` or `I18n.translate()` in Ruby

```json
{
  "id": "rule-ruby-i18n-full",
  "language": ["RUBY"],
  "trigger": "CALL_ARGUMENT",
  "priority": 20,
  "exclude": false,
  "constraints": [
    { "type": "CALLABLE_NAME", "value": "^(t|translate)$", "matchMode": "REGEX", "negated": false },
    { "type": "RECEIVER_TYPE_FQN", "value": "I18n", "matchMode": "EXACT", "negated": false }
  ]
}
```

### Match `ResourceBundle.getString()` in Java

```json
{
  "id": "rule-java-ResourceBundle",
  "language": ["JAVA"],
  "trigger": "CALL_ARGUMENT",
  "priority": 10,
  "exclude": false,
  "constraints": [
    { "type": "CALLABLE_FQN", "value": "java.util.ResourceBundle.getString", "matchMode": "EXACT", "negated": false },
    { "type": "ARGUMENT_INDEX", "value": "0", "matchMode": "EXACT", "negated": false }
  ]
}
```

### Exclude test source files from any assistance

```json
{
  "id": "rule-exclude-tests",
  "language": [],
  "trigger": "UNKNOWN",
  "priority": 999,
  "exclude": true,
  "constraints": [
    { "type": "IN_TEST_SOURCES", "value": "true", "matchMode": "EXACT", "negated": false }
  ]
}
```
