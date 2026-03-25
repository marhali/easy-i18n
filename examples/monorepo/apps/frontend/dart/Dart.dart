import 'package:i18n/i18n.dart';
import 'package:i18n/localizations.dart';

// All examples use call argument (argument index 0)

// --- tr() — free function shorthand (rule-dart-tr) ---

// Resolved keys → folding, hover documentation, and Ctrl+Click reference work
void freeFunctionCalls() {
  print(tr("common:primitive.string.sample"));
  print(tr("user:object.deeply.nested.structure.description"));
  print(tr("billing:object.deeply.nested.structure.description"));
  print(tr("common:object.hybrid\.flat\.structure"));

  // Unresolved keys → inspection error
  print(tr("common:does.not.exist"));
  print(tr("user:unknown.key"));
}

// --- localizations.translate() — instance method call (rule-dart-localizations-translate) ---
// Matches: receiver = "localizations", callable = "translate"

void methodCall(AppLocalizations localizations) {
  // Resolved keys → folding, hover documentation, and Ctrl+Click reference work
  print(localizations.translate("billing:primitive.string.sample"));
  print(localizations.translate("common:primitive.string.sample"));

  // Unresolved key → inspection error
  print(localizations.translate("user:unknown.key"));

  // Hard coded literal
  print("Any hard coded literal")
}
